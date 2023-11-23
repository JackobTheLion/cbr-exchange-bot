package org.telegram.cbrexchangebot.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "rates")
@IdClass(RatePK.class)
public class Rate {
    @Id
    private Long numCode;
    @Id
    private LocalDate date;

    private Integer nominal;

    private Double rate;

    private Double vunitRate;
}
