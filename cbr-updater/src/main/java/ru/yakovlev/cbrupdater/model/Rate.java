package ru.yakovlev.cbrupdater.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "rates")
@IdClass(RatePK.class)
public class Rate implements Serializable {
    @Id
    private Long numCode;
    @Id
    private LocalDate date;

    @ManyToOne
    @JoinColumn(name = "numCode", insertable = false, updatable = false)
    private Currency currency;

    private Integer nominal;

    private Double value;

    private Double vunitRate;
}
