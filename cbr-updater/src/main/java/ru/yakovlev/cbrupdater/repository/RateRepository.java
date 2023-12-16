package ru.yakovlev.cbrupdater.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.yakovlev.cbrupdater.model.Rate;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface RateRepository extends JpaRepository<Rate, Long> {
    @Query(value = "select date from rates as r group by r.date order by r.date desc limit 1", nativeQuery = true)
    Optional<LocalDate> getLatestDate();
}
