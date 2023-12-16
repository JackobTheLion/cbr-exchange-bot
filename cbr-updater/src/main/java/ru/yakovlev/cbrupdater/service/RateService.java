package ru.yakovlev.cbrupdater.service;

import jakarta.annotation.Nullable;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import ru.yakovlev.cbrupdater.dto.CurrencyCbrDto;
import ru.yakovlev.cbrupdater.model.Currency;
import ru.yakovlev.cbrupdater.model.Rate;
import ru.yakovlev.cbrupdater.repository.CurrencyRepository;
import ru.yakovlev.cbrupdater.repository.RateRepository;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static ru.yakovlev.cbrupdater.mapper.CurrencyMapper.mapCurrencyFromDto;
import static ru.yakovlev.cbrupdater.mapper.RateMapper.mapRateFromDto;

/**
 * Service for automatic update of currency rates in DB.
 */

@Service
@Slf4j
public class RateService {
    private final CurrencyRepository currencyRepository;
    private final RateRepository rateRepository;
    private final CbrClient cbrClient;

    private final LocalDate latestUpdate;

    /**
     * Cache of currencies already existing in DB.
     */
    private Set<String> knownCurrencies;

    public RateService(CurrencyRepository currencyRepository,
                       RateRepository rateRepository,
                       CbrClient cbrClient,
                       @Value("${org.telegram.cbrexchangebot.earliestRateDate:no_value}") String date) {

        this.currencyRepository = currencyRepository;
        this.rateRepository = rateRepository;
        this.cbrClient = cbrClient;
        this.latestUpdate = getRatesHistoryStartDate(date);
    }

    /**
     * Automatic rates exchange update every Monday-Friday at 17:01, after CBR publication.
     */
    @Scheduled(cron = "1 17 * * MON-FRI")
    private void autoRateUpdate() {
        log.warn("Auto update initialized at: {}.", LocalDateTime.now());
        getAndSaveRate(LocalDate.now());
    }

    /**
     * Gets rates of exchange as of come date.
     *
     * @param date
     */
    private void getAndSaveRate(@Nullable LocalDate date) {
        if (date == null) {
            date = LocalDate.now();
        }
        List<CurrencyCbrDto> exchangeRates = cbrClient.getExchangeRates(date);
        checkCurrencies(exchangeRates);
        for (CurrencyCbrDto currencyCbrDto : exchangeRates) {
            Rate rate = mapRateFromDto(currencyCbrDto);
            rateRepository.save(rate);
        }
        log.info("Rates were updated.");
    }

    /**
     * Checks whether new currencies were added by Central bank and if yes, saves new currency to DB and memory.
     *
     * @param exchangeRates list of currencies received from CBR.
     */
    private void checkCurrencies(List<CurrencyCbrDto> exchangeRates) {
        for (CurrencyCbrDto currencyCbrDto : exchangeRates) {
            if (!knownCurrencies.contains(currencyCbrDto.getCharCode())) {
                Currency currency = mapCurrencyFromDto(currencyCbrDto);
                currencyRepository.save(currency);
                knownCurrencies.add(currency.getCharCode());
                log.warn("New currency added: {}.", currency);
            }
        }
    }

    /**
     * Method sets up history start date for rates exchange. Date is set up by user in application properties.
     * If date is not set or set incorrect, date will be automatically set as 30 days before. This is achieved
     * by wrong default values as "no_values" (see constructor).
     *
     * @param date input by user.
     * @return generated rates start history date.
     */
    private static LocalDate getRatesHistoryStartDate(String date) {
        try {
            LocalDate parsedDate = LocalDate.parse(date);
            log.info("Initial rates date set: {}.", parsedDate);
            return parsedDate;
        } catch (DateTimeParseException e) {
            LocalDate now = LocalDate.now().minusDays(30);
            log.info("Date: '{}' cannot be parsed. Setting default date: {}", date, now);
            return now;
        }
    }

    @PostConstruct
    private void initKnownCurrenciesAndUpdateRates() {
        updateRates();
        initCurrencies();
    }

    /**
     * Method run at start-up. <p>
     * Checks latest rates date in DB and if it is before then latestDate, updates it. <p>
     * If DB is empty latest date is considered as latest date set by settings.
     */
    private void updateRates() {
        LocalDate date = rateRepository.getLatestDate().orElse(latestUpdate);
        log.info("Latest rates available: {}.", date);

        while (date.isBefore(LocalDate.now().plusDays(1)) && date.getDayOfWeek() != DayOfWeek.SATURDAY) {
            getAndSaveRate(date);
            log.info("Rates updated for: {}.", date);
            date = date.plusDays(1);
        }
    }

    /**
     * Caches existing currencies into memory in order to monitor new currencies which might be introduced by central
     * bank. Originally updated right after DB filling.
     */
    private void initCurrencies() {
        this.knownCurrencies = currencyRepository.findAll().stream().map(Currency::getCharCode)
                .collect(Collectors.toSet());
    }
}
