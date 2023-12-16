package ru.yakovlev.cbrupdater.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.yakovlev.cbrupdater.model.Currency;

@Repository
public interface CurrencyRepository extends JpaRepository<Currency, Long> {
}
