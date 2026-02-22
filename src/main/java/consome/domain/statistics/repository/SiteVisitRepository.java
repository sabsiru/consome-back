package consome.domain.statistics.repository;

import consome.domain.statistics.entity.SiteVisit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.Optional;

public interface SiteVisitRepository extends JpaRepository<SiteVisit, Long> {

    Optional<SiteVisit> findByVisitorKeyAndVisitDate(String visitorKey, LocalDate visitDate);

    @Query("SELECT COUNT(DISTINCT sv.visitorKey) FROM SiteVisit sv")
    long countTotalUniqueVisitors();

    @Query("SELECT COUNT(sv) FROM SiteVisit sv WHERE sv.visitDate = :date")
    long countByVisitDate(@Param("date") LocalDate date);

    boolean existsByVisitorKeyAndVisitDate(String visitorKey, LocalDate visitDate);
}
