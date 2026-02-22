package consome.domain.statistics.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(
    name = "site_visit",
    uniqueConstraints = @UniqueConstraint(columnNames = {"visitor_key", "visit_date"})
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SiteVisit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "visitor_key", nullable = false)
    private String visitorKey;  // IP 또는 "user:{userId}"

    @Column(name = "visit_date", nullable = false)
    private LocalDate visitDate;

    @Column(name = "first_visit_at", nullable = false)
    private LocalDateTime firstVisitAt;

    private SiteVisit(String visitorKey, LocalDate visitDate) {
        this.visitorKey = visitorKey;
        this.visitDate = visitDate;
        this.firstVisitAt = LocalDateTime.now();
    }

    public static SiteVisit create(String visitorKey) {
        return new SiteVisit(visitorKey, LocalDate.now());
    }

    public static String buildVisitorKey(Long userId, String ip) {
        return userId != null ? "user:" + userId : ip;
    }
}
