package consome.domain.point;

import consome.domain.common.exception.BusinessException;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Point {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private int userPoint;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    private Point(Long userId) {
        this.userId = userId;
        this.userPoint = 100;
        this.updatedAt = LocalDateTime.now();
    }

    public static Point initialize(Long userId) {
        return new Point(userId);
    }

    public void earn(int amount) {
        if (amount < 0) {
            throw new BusinessException.InvalidPointAmount("적립할 포인트는 0 이상이어야 합니다.");
        }
        this.userPoint += amount;
        this.updatedAt = LocalDateTime.now();
    }

    public void penalize(int amount) {
        if (amount < 0) {
            throw new BusinessException.InvalidPointAmount("차감할 포인트는 0 이상이어야 합니다.");
        }
        this.userPoint -= amount;
        this.updatedAt = LocalDateTime.now();
    }
}