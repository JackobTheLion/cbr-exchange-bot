package ru.yakovlev.cbrupdater.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import ru.yakovlev.cbrupdater.repository.CurrencyRepository;
import ru.yakovlev.cbrupdater.repository.RateRepository;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


@Service
@Slf4j
public class RateService {
    private final CurrencyRepository currencyRepository;
    private final RateRepository rateRepository;
    private final CbrClient cbrClient;

    private final Set<String> knownCurrencies;
    private final LocalDate latestUpdate;

    public RateService(CurrencyRepository currencyRepository, RateRepository rateRepository, CbrClient cbrClient,
                       @Value("${org.telegram.cbrexchangebot.earliestRateDate:no_value}") String date) {
        this.currencyRepository = currencyRepository;
        this.rateRepository = rateRepository;
        this.cbrClient = cbrClient;
        this.latestUpdate = initLatestUpdate(date);
        this.knownCurrencies = initCurrencies();
        //updateRates();
    }

    public List<CurrencyCbrDto> getRates() { //TODO method marked for deletion
        List<CurrencyCbrDto> exchangeRates = cbrClient.getExchangeRates(null);
        checkCurrencies(exchangeRates);
        saveRates(null);
        return exchangeRates;
    }

    public Rate getRate(String currencyCode) {
        log.info("Looking for currency '{}'", currencyCode);
        return rateRepository.findByLatestRate(currencyCode.toUpperCase())
                .orElseThrow(() -> {
                    log.info("Currency '{}' not found", currencyCode);
                    return new NoSuchRateException(String.format("Currency \"%s\" not found", currencyCode));
                });
    }

    public List<String> getKnownCurrencies() {
        List<String> currencies = new ArrayList<String>(knownCurrencies);
        currencies.sort(String::compareTo);
        return currencies;
    }

    private void saveRates(LocalDate date) {
        List<CurrencyCbrDto> exchangeRates = cbrClient.getExchangeRates(date);
        checkCurrencies(exchangeRates);
        for (CurrencyCbrDto currencyCbrDto : exchangeRates) {
            Rate rate = mapRateFromDto(currencyCbrDto);
            rateRepository.save(rate);
        }
        log.info("Rates were updated.");
    }

    @Scheduled(cron = "1 17 * * MON-FRI")
    private void autoRateUpdate() {
        log.warn("Auto update initialized at: {}.", LocalDateTime.now());
        saveRates(null);
    }

    private void checkCurrencies(List<CurrencyCbrDto> exchangeRates) {
        for (CurrencyCbrDto currencyCbrDto : exchangeRates) {
            if (!knownCurrencies.contains(currencyCbrDto.getCharCode())) {
                Currency currency = mapCurrencyFromDto(currencyCbrDto);
                currencyRepository.save(currency);
                knownCurrencies.add(currency.getCharCode());
                log.info("New currency saved: {}.", currency);
            }
        }
    }

    private Set<String> initCurrencies() {
        return currencyRepository.findAll().stream().map(Currency::getCharCode).collect(Collectors.toSet());
    }

    private LocalDate initLatestUpdate(String date) {
        try {
            LocalDate parsedDate = LocalDate.parse(date);
            log.info("Initial rates date set: {}.", parsedDate);
            return parsedDate;
        } catch (DateTimeParseException e) {
            LocalDate now = LocalDate.now().minusDays(14);
            log.info("Date: '{}' cannot be parsed. Setting default date: {}", date, now);
            return now;
        }
    }

    private void updateRates() {
        LocalDate date = rateRepository.getLatestDate().orElse(latestUpdate);
        log.info("Latest rates available: {}.", date);
        while (date.isBefore(LocalDate.now().plusDays(1)) && date.getDayOfWeek() != DayOfWeek.SATURDAY) {
            saveRates(date);
            log.info("Rates updated for: {}.", date);
            date = date.plusDays(1);
        }
    }
}
