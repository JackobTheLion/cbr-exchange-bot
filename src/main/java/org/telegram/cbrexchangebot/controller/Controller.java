package org.telegram.cbrexchangebot.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.telegram.cbrexchangebot.dto.CurrencyCbrDto;
import org.telegram.cbrexchangebot.model.RateShort;
import org.telegram.cbrexchangebot.service.CbrClient;
import org.telegram.cbrexchangebot.service.RateService;

import java.util.List;

@RestController
@Slf4j
@AllArgsConstructor
public class Controller {

    private CbrClient cbrClient;
    private final RateService rateService;

    @GetMapping("/")
    public List<CurrencyCbrDto> test() {
        return rateService.getRates();
    }

    @GetMapping("/currency")
    public RateShort test2(@RequestParam String currencyCode) {
        return rateService.getRate(currencyCode);
    }
}
