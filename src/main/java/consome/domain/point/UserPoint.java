package consome.domain.point;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserPoint {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private int point;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    private UserPoint(Long userId) {
        this.userId = userId;
        this.point = 0;
        this.updatedAt = LocalDateTime.now();
    }

    public static UserPoint initialize(Long userId) {
        return new UserPoint(userId);
    }

    public void increase(int amount) {
        this.point += amount;
        this.updatedAt = LocalDateTime.now();
    }

    public void decrease(int amount) {
        this.point -= amount;
        this.updatedAt = LocalDateTime.now();
    }
}