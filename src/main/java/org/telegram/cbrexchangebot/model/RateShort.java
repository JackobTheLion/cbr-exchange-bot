package org.telegram.cbrexchangebot.model;

import java.time.LocalDate;

public interface RateShort {
    String getChar_Code();
    String getName();
    LocalDate getDate();
    Integer getNominal();
    Double getValue();
}
