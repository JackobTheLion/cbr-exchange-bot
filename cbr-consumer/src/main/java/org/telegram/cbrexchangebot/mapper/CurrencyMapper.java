package org.telegram.cbrexchangebot.mapper;

import org.telegram.cbrexchangebot.dto.CurrencyCbrDto;
import org.telegram.cbrexchangebot.model.Currency;

public class CurrencyMapper {
    public static Currency mapCurrencyFromDto(CurrencyCbrDto currencyCbrDto) {
        return Currency.builder()
                .numCode(currencyCbrDto.getNumCode())
                .charCode(currencyCbrDto.getCharCode())
                .name(currencyCbrDto.getName())
                .build();
    }


}
