package consome.domain.post.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PostView {

    @Id @GeneratedValue
    private Long id;

    @Column(nullable = false)
    private Long postId;

    @Column(nullable = false)
    private String userIp;

    private Long userId;

    @Column(nullable = false)
    private LocalDateTime lastViewedAt;

    private PostView(Long postId, String userIp, Long userId) {
        this.postId = postId;
        this.userIp = userIp;
        this.userId = userId;
        this.lastViewedAt = LocalDateTime.now();
    }

    public static PostView create(Long postId, String userIp, Long userId) {
        Long resolvedUserId = (userId == null) ? 0L : userId;
        return new PostView(postId, userIp, resolvedUserId);
    }


}
