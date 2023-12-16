package org.telegram.cbrexchangebot.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.cbrexchangebot.exception.NoSuchRateException;
import org.telegram.cbrexchangebot.model.Rate;
import org.telegram.cbrexchangebot.repository.RateRepository;

import java.util.ArrayList;
import java.util.List;


@Service
@Slf4j
@RequiredArgsConstructor
public class RateService {

    private final RateRepository rateRepository;

    public Rate getRate(String currencyCode) {
        log.info("Looking for currency '{}'", currencyCode);
        return rateRepository.findByLatestRate(currencyCode.toUpperCase())
                .orElseThrow(() -> {
                    log.info("Currency '{}' not found", currencyCode);
                    return new NoSuchRateException(String.format("Currency \"%s\" not found", currencyCode));
                });
    }

    public List<String> getKnownCurrencies() {
        List<String> currencies = new ArrayList<>();
        currencies.sort(String::compareTo);
        return currencies;
    }
}
