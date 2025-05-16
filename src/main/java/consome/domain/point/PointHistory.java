package consome.domain.point;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PointHistory {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private int amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PointHistoryType type;

    @Column(nullable = false)
    private String reason;

    @Column(nullable = false)
    private int pointAfter;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    private PointHistory(Long userId, int amount, PointHistoryType type, String reason, int pointAfter) {
        this.userId = userId;
        this.amount = amount;
        this.type = type;
        this.reason = reason;
        this.pointAfter = pointAfter;
        this.createdAt = LocalDateTime.now();
    }

    public static PointHistory gain(Long userId, int amount, String reason, int pointAfter) {
        return new PointHistory(userId, amount, PointHistoryType.GAIN, reason, pointAfter);
    }

    public static PointHistory spend(Long userId, int amount, String reason, int pointAfter) {
        return new PointHistory(userId, amount, PointHistoryType.SPEND, reason, pointAfter);
    }

    public static PointHistory penalty(Long userId, int amount, String reason, int pointAfter) {
        return new PointHistory(userId, amount, PointHistoryType.PENALTY, reason, pointAfter);
    }
}