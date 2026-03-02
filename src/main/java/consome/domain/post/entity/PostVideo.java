package consome.domain.post.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PostVideo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long postId;

    @Column(nullable = false)
    private String url;

    @Column(nullable = false)
    private String storedName;

    @Column(nullable = false)
    private String originalName;

    @Column(nullable = false)
    private Long fileSize;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private boolean deleted = false;

    public static PostVideo create(Long postId, String url, String storedName,
                                   String originalName, Long fileSize) {
        PostVideo postVideo = new PostVideo();
        postVideo.postId = postId;
        postVideo.url = url;
        postVideo.storedName = storedName;
        postVideo.originalName = originalName;
        postVideo.fileSize = fileSize;
        postVideo.createdAt = LocalDateTime.now();
        return postVideo;
    }

    public void delete() {
        this.deleted = true;
    }
}
