package org.telegram.cbrexchangebot.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriBuilderFactory;
import org.telegram.cbrexchangebot.dto.CurrencyCbrDto;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.Reader;
import java.io.StringReader;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Component
@Slf4j
public class CbrClient {
    private final RestTemplate rest;

    public CbrClient(RestTemplateBuilder builder) {
        rest = builder
                .rootUri("https://www.cbr.ru")
                .build();
    }

    public List<CurrencyCbrDto> getExchangeRates(@Nullable LocalDate date) {
        String url = getUrl(date);

        String xml = rest.getForEntity(url, String.class).getBody();
        List<CurrencyCbrDto> rates = new ArrayList<>();

        try (Reader reader = new StringReader(xml)) {
            DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document doc = db.parse(new InputSource(reader));

            LocalDate localDate = getDate(doc);
            log.info("Received rates for date: {}", localDate);

            NodeList list = doc.getElementsByTagName("Valute");

            for (int i = 0; i < list.getLength(); i++) {
                Node node = list.item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element element = (Element) node;

                    CurrencyCbrDto rate = CurrencyCbrDto.builder()
                            .numCode(Long.parseLong(element.getElementsByTagName("NumCode").item(0).getTextContent()))
                            .charCode(element.getElementsByTagName("CharCode").item(0).getTextContent())
                            .date(localDate)
                            .nominal(Integer.valueOf(element.getElementsByTagName("Nominal").item(0).getTextContent()))
                            .name(element.getElementsByTagName("Name").item(0).getTextContent())
                            .value(makeDouble(element.getElementsByTagName("Value").item(0).getTextContent()))
                            .vunitRate(makeDouble(element.getElementsByTagName("VunitRate").item(0).getTextContent()))
                            .build();
                    rates.add(rate);
                }
            }
        } catch (Exception ex) {
            log.error("xml parsing error, xml: {}", xml, ex);
        }
        return rates;
    }

    private LocalDate getDate(Document doc) throws ParseException {
        Element root = doc.getDocumentElement();
        DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
        Date date = dateFormat.parse(root.getAttribute("Date"));
        return LocalDate.ofInstant(date.toInstant(), ZoneId.of("Europe/Moscow"));
    }

    private String getUrl(LocalDate date) {
        if (date != null) {
            DateTimeFormatter formatters = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            String text = date.format(formatters);
            return "/scripts/XML_daily.asp?date_req=".concat(text);
        } else return "/scripts/XML_daily.asp";
    }

    private double makeDouble(String s) {
        return Double.parseDouble(s.replace(",", "."));
    }

}
