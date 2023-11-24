package org.telegram.cbrexchangebot.mapper;

import org.telegram.cbrexchangebot.dto.CurrencyCbrDto;
import org.telegram.cbrexchangebot.model.Rate;

public class RateMapper {
    public static Rate mapRateFromDto(CurrencyCbrDto currencyCbrDto) {
        return Rate.builder()
                .numCode(currencyCbrDto.getNumCode())
                .currency(CurrencyMapper.mapCurrencyFromDto(currencyCbrDto))
                .date(currencyCbrDto.getDate())
                .nominal(currencyCbrDto.getNominal())
                .value(currencyCbrDto.getValue())
                .vunitRate(currencyCbrDto.getVunitRate())
                .build();
    }
}
