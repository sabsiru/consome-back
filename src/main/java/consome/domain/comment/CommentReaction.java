package consome.domain.comment;

import consome.domain.post.ReactionType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CommentReaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long commentId;

    @Column(nullable = false)
    private Long userId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReactionType type;

    @Column(nullable = false)
    private boolean deleted = false;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private CommentReaction(Long commentId, Long userId, ReactionType type) {
        this.commentId = commentId;
        this.userId = userId;
        this.type = type;
        this.createdAt = LocalDateTime.now();
    }

    public static CommentReaction like(Long commentId, Long userId) {
        return new CommentReaction(commentId, userId, ReactionType.LIKE);
    }

    public static CommentReaction dislike(Long commentId, Long userId) {
        return new CommentReaction(commentId, userId, ReactionType.DISLIKE);
    }

    public void cancel() {
        this.deleted = true;
        this.updatedAt = LocalDateTime.now();
    }
}
