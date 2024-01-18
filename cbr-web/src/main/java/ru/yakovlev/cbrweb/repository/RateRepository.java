package ru.yakovlev.cbrweb.repository;


import java.time.LocalDate;
import java.util.Optional;

/*@Repository
public interface RateRepository extends JpaRepository<Rate, Long> {
    @Query(value = "SELECT * FROM rates as r LEFT JOIN currencies c on " +
            "c.num_code = r.num_code where r.date = ?1 AND c.char_code = ?2", nativeQuery = true)
    Optional<Rate> findByDateAndCurrency(LocalDate date, String charCode);

    @Query(value = "SELECT * FROM rates as r LEFT JOIN currencies c on " +
            "c.num_code = r.num_code where c.char_code = ?1 order by r.date desc limit 1", nativeQuery = true)
    Optional<Rate> findByLatestRate(String charCode);

    @Query(value = "select date from rates as r group by r.date order by r.date desc limit 1", nativeQuery = true)
    Optional<LocalDate> getLatestDate();
}*/
