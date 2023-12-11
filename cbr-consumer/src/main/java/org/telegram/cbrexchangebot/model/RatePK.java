package org.telegram.cbrexchangebot.model;

import lombok.*;

import java.io.Serializable;
import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@Getter
@Setter
public class RatePK implements Serializable {
    private Long numCode;
    private LocalDate date;
}
