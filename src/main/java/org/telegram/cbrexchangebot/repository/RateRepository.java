package org.telegram.cbrexchangebot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.telegram.cbrexchangebot.model.Currency;
import org.telegram.cbrexchangebot.model.Rate;
import org.telegram.cbrexchangebot.model.RateShort;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface RateRepository extends JpaRepository<Rate, Currency> {
    @Query(value = "SELECT date, nominal, value, char_code, name FROM RATES as r LEFT JOIN currencies c on " +
            "c.num_code = r.num_code where r.date = ?1 AND c.char_code = ?2", nativeQuery = true)
    Optional<RateShort> findByDateAndCurrency(LocalDate date, String charCode);
}
