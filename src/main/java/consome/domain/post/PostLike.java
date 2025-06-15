package consome.domain.post;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PostLike {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long postId;

    @Column(nullable = false)
    private Long userId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private LikeType type;

    @Column(nullable = false)
    private boolean deleted = false;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    private PostLike(Long postId, Long userId, LikeType type) {
        this.postId = postId;
        this.userId = userId;
        this.type = type;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public static PostLike like(Long postId, Long userId) {
        return new PostLike(postId, userId, LikeType.LIKE);
    }

    public static PostLike disLike(Long postId, Long userId) {
        return new PostLike(postId, userId, LikeType.DISLIKE);
    }

    public void cancel() {
        this.deleted = true;
        this.updatedAt = LocalDateTime.now();
    }


}