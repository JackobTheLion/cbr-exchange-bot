package ru.yakovlev.cbrupdater.mapper;


import ru.yakovlev.cbrupdater.dto.CurrencyCbrDto;
import ru.yakovlev.cbrupdater.model.Currency;

public class CurrencyMapper {
    public static Currency mapCurrencyFromDto(CurrencyCbrDto currencyCbrDto) {
        return Currency.builder()
                .numCode(currencyCbrDto.getNumCode())
                .charCode(currencyCbrDto.getCharCode())
                .name(currencyCbrDto.getName())
                .build();
    }


}
