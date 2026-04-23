package consome.domain.statistics.repository;

import consome.domain.statistics.ActivityType;
import consome.domain.statistics.entity.HourlyActivity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface HourlyActivityRepository extends JpaRepository<HourlyActivity, Long> {

    Optional<HourlyActivity> findByActivityDateAndHourAndType(LocalDate activityDate, int hour, ActivityType type);

    @Query("SELECT ha FROM HourlyActivity ha " +
           "WHERE ha.activityDate >= :startDate AND ha.activityDate <= :endDate")
    List<HourlyActivity> findAllByDateRange(
        @Param("startDate") LocalDate startDate,
        @Param("endDate") LocalDate endDate
    );

    @Query("SELECT ha FROM HourlyActivity ha " +
           "WHERE ha.activityDate >= :startDate AND ha.activityDate <= :endDate " +
           "AND ha.type = :type")
    List<HourlyActivity> findAllByDateRangeAndType(
        @Param("startDate") LocalDate startDate,
        @Param("endDate") LocalDate endDate,
        @Param("type") ActivityType type
    );
}
