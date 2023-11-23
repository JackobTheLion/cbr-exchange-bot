package org.telegram.cbrexchangebot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.telegram.cbrexchangebot.model.Currency;

@Repository
public interface CurrencyRepository extends JpaRepository<Currency, Long> {
}
