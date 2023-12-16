package ru.yakovlev.cbrupdater.mapper;


import ru.yakovlev.cbrupdater.dto.CurrencyCbrDto;
import ru.yakovlev.cbrupdater.model.Rate;

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
