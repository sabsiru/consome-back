package consome.domain.post;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PostReaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long postId;

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

    private PostReaction(Long postId, Long userId, ReactionType type) {
        this.postId = postId;
        this.userId = userId;
        this.type = type;
        this.createdAt = LocalDateTime.now();
    }

    public static PostReaction like(Long postId, Long userId) {
        return new PostReaction(postId, userId, ReactionType.LIKE);
    }

    public static PostReaction disLike(Long postId, Long userId) {
        return new PostReaction(postId, userId, ReactionType.DISLIKE);
    }

    public void cancel() {
        this.deleted = true;
        this.updatedAt = LocalDateTime.now();
    }


}