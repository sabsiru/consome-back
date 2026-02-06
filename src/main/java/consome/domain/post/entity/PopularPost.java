package consome.domain.post.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "popular_post", indexes = {
        @Index(name = "idx_popular_post_created_at", columnList = "createdAt DESC")
})
public class PopularPost {

    @Id
    private Long postId;

    @Column(nullable = false)
    private Long boardId;

    @Column(nullable = false)
    private int viewCount;

    @Column(nullable = false)
    private int likeCount;

    @Column(nullable = false)
    private int commentCount;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    private PopularPost(Long postId, Long boardId, int viewCount, int likeCount, int commentCount) {
        this.postId = postId;
        this.boardId = boardId;
        this.viewCount = viewCount;
        this.likeCount = likeCount;
        this.commentCount = commentCount;
        this.createdAt = LocalDateTime.now();
    }

    public static PopularPost create(Long postId, Long boardId, int viewCount, int likeCount, int commentCount) {
        return new PopularPost(postId, boardId, viewCount, likeCount, commentCount);
    }
}
