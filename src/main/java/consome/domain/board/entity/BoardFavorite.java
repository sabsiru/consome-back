package consome.domain.board.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "board_favorite", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"user_id", "board_id"})
})
public class BoardFavorite {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "board_id", nullable = false)
    private Long boardId;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    private BoardFavorite(Long userId, Long boardId) {
        this.userId = userId;
        this.boardId = boardId;
        this.createdAt = LocalDateTime.now();
    }

    public static BoardFavorite of(Long userId, Long boardId) {
        return new BoardFavorite(userId, boardId);
    }
}
