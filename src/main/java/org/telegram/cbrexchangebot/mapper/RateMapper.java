package org.telegram.cbrexchangebot.mapper;

import org.telegram.cbrexchangebot.dto.CurrencyCbrDto;
import org.telegram.cbrexchangebot.dto.RateResponseDto;
import org.telegram.cbrexchangebot.model.Rate;
import org.telegram.cbrexchangebot.model.RateShort;

public class RateMapper {
    public static Rate mapRateFromDto(CurrencyCbrDto currencyCbrDto) {
        return Rate.builder()
                .numCode(Long.valueOf(currencyCbrDto.getNumCode()))
                .date(currencyCbrDto.getDate())
                .nominal(currencyCbrDto.getNominal())
                .rate(currencyCbrDto.getValue())
                .vunitRate(currencyCbrDto.getVunitRate())
                .build();
    }
}
