package org.telegram.cbrexchangebot.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.telegram.cbrexchangebot.dto.CurrencyCbrDto;
import org.telegram.cbrexchangebot.model.Currency;
import org.telegram.cbrexchangebot.model.Rate;
import org.telegram.cbrexchangebot.model.RateShort;
import org.telegram.cbrexchangebot.repository.CurrencyRepository;
import org.telegram.cbrexchangebot.repository.RateRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.telegram.cbrexchangebot.mapper.CurrencyMapper.mapCurrencyFromDto;
import static org.telegram.cbrexchangebot.mapper.RateMapper.*;

@Service
@Slf4j
@AllArgsConstructor
public class RateService {
    private final CurrencyRepository currencyRepository;
    private final RateRepository rateRepository;
    private final CbrClient cbrClient;

    public List<CurrencyCbrDto> getRates() {
        List<CurrencyCbrDto> exchangeRates = cbrClient.getExchangeRates();
        checkCurrencies(exchangeRates);
        updateRates();

        return exchangeRates;
    }

    public RateShort getRate(String currencyCode) {
        Optional<RateShort> rate = rateRepository.findByDateAndCurrency(LocalDate.now(), currencyCode);
        return rate.get();
    }

    public Currency addCurrency(Currency currency) {
        log.warn("Adding new currency: {}", currency);
        return currencyRepository.save(currency);
    }

    @Scheduled(cron = "0 17 * * *")
    private void updateRates() {
        List<CurrencyCbrDto> exchangeRates = cbrClient.getExchangeRates();
        checkCurrencies(exchangeRates);

        for (CurrencyCbrDto currencyCbrDto :exchangeRates) {
            Rate rate = mapRateFromDto(currencyCbrDto);
            rateRepository.save(rate);
        }
        log.info("Rates were updated.");
    }

    private void checkCurrencies(List<CurrencyCbrDto> exchangeRates) {
        for (CurrencyCbrDto currencyCbrDto : exchangeRates) {
            Currency currency = mapCurrencyFromDto(currencyCbrDto);
            currencyRepository.findById(currency.getNumCode()).orElseGet(() -> addCurrency(currency));
        }
    }

}
