package consome.domain.comment;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CommentLike {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long commentId;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private boolean deleted = false;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    private CommentLike(Long commentId, Long userId) {
        this.commentId = commentId;
        this.userId = userId;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public static CommentLike create(Long commentId, Long userId) {
        return new CommentLike(commentId, userId);
    }

    public void cancel() {
        this.deleted = true;
        this.updatedAt = LocalDateTime.now();
    }

    public void restore() {
        this.deleted = false;
        this.updatedAt = LocalDateTime.now();
    }
}