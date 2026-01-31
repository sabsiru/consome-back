package consome.domain.admin;

import consome.domain.user.Role;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Getter
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "board_manager", uniqueConstraints = @UniqueConstraint(columnNames = {"boardId", "userId"}))
public class BoardManager {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long boardId;

    @Column(nullable = false)
    private Long userId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    private BoardManager(Long boardId, Long userId, Role role) {
        this.boardId = boardId;
        this.userId = userId;
        this.role = role;
    }

    public static BoardManager create(Long boardId, Long userId) {
        return new BoardManager(boardId, userId, Role.MANAGER);
    }
}
