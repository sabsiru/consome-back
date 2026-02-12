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
    @Column(nullable = false, length = 50)
    private PointHistoryType type;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private int beforePoint;

    @Column(nullable = false)
    private int afterPoint;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    private PointHistory(Long userId, int amount, PointHistoryType type, int beforePoint, int afterPoint) {
        this.userId = userId;
        this.amount = amount;
        this.type = type;
        this.description = type.getDescription();
        this.beforePoint = beforePoint;
        this.afterPoint = afterPoint;
        this.createdAt = LocalDateTime.now();
    }

    public static PointHistory create(Long userId, int amount, PointHistoryType type, int beforePoint, int afterPoint) {
        return new PointHistory(userId, amount, type, beforePoint, afterPoint);
    }

}