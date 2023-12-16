package ru.yakovlev.cbrupdater.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * Dto corresponding with data received from CBR.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CurrencyCbrDto {
    private Long numCode;

    private String charCode;

    private LocalDate date;

    private Integer nominal;

    private String name;

    private Double value;

    private Double vunitRate;
}
