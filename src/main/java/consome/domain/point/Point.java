package consome.domain.point;

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
    private Long refUserId;

    @Column(nullable = false)
    private int point;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    private Point(Long refUserId) {
        this.refUserId = refUserId;
        this.point = 100;
        this.updatedAt = LocalDateTime.now();
    }

    public static Point initialize(Long userId) {
        return new Point(userId);
    }

    public void earn(int amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("적립할 포인트는 0 이상이어야 합니다.");
        }
        this.point += amount;
        this.updatedAt = LocalDateTime.now();
    }

    public void penalize(int amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("차감할 포인트는 0 이상이어야 합니다.");
        }
        this.point -= amount;
        this.updatedAt = LocalDateTime.now();
    }
}