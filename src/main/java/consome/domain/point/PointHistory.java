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

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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
    private int currentPoint;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    private PointHistory(Long userId, int amount, PointHistoryType type, String reason, int currentPoint) {
        this.userId = userId;
        this.amount = amount;
        this.type = type;
        this.reason = reason;
        this.currentPoint = currentPoint;
        this.createdAt = LocalDateTime.now();
    }

    public static PointHistory earn(Long userId, int amount, String reason, int currentPoint) {
        return new PointHistory(userId, amount, PointHistoryType.EARN, reason, currentPoint);
    }

    public static PointHistory penalize(Long userId, int amount, String reason, int currentPoint) {
        return new PointHistory(userId, amount, PointHistoryType.PENALIZE, reason, currentPoint);
    }

}