package org.telegram.cbrexchangebot.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CurrencyCbrDto {
    private String numCode;

    private String charCode;

    private LocalDate date;

    private Integer nominal;

    private String name;

    private Double value;

    private Double vunitRate;
}
