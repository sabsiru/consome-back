package consome.domain.statistics.entity;

import consome.domain.statistics.ActivityType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(
    name = "hourly_activity",
    uniqueConstraints = @UniqueConstraint(
        name = "uk_hourly_activity_date_hour_type",
        columnNames = {"activity_date", "hour", "type"}
    ),
    indexes = @Index(name = "idx_hourly_activity_date", columnList = "activity_date")
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class HourlyActivity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "activity_date", nullable = false)
    private LocalDate activityDate;

    @Column(name = "hour", nullable = false)
    private int hour;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, length = 16)
    private ActivityType type;

    @Column(name = "count", nullable = false)
    private long count;

    private HourlyActivity(LocalDate activityDate, int hour, ActivityType type, long count) {
        this.activityDate = activityDate;
        this.hour = hour;
        this.type = type;
        this.count = count;
    }

    public static HourlyActivity of(LocalDate activityDate, int hour, ActivityType type, long count) {
        return new HourlyActivity(activityDate, hour, type, count);
    }

    public void addCount(long delta) {
        this.count += delta;
    }
}
