package consome.domain.level.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserLevel {

    @Id
    private Long userId;

    @Column(nullable = false)
    private int level;

    @Column(nullable = false)
    private int totalExp;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    private UserLevel(Long userId, int level, int totalExp) {
        this.userId = userId;
        this.level = level;
        this.totalExp = totalExp;
        this.updatedAt = LocalDateTime.now();
    }

    public static UserLevel initialize(Long userId, int initialExp) {
        return new UserLevel(userId, 1, initialExp);
    }

    public void updateLevel(int newLevel) {
        this.level = newLevel;
        this.updatedAt = LocalDateTime.now();
    }

    public void syncExp(int currentPoint) {
        this.totalExp = currentPoint;
        this.updatedAt = LocalDateTime.now();
    }
}
