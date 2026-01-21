package consome.domain.post.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PostImage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long postId;

    @Column(nullable = false)
    private String url;

    @Column(nullable = false)
    private String storedName;    // UUID 파일명

    @Column(nullable = false)
    private String originalName;

    @Column(nullable = false)
    private Long fileSize;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private boolean deleted = false;

    public static PostImage create(Long postId, String url, String storedName,
                                   String originalName, Long fileSize) {
        PostImage postImage = new PostImage();
        postImage.postId = postId;
        postImage.url = url;
        postImage.storedName = storedName;
        postImage.originalName = originalName;
        postImage.fileSize = fileSize;
        postImage.createdAt = LocalDateTime.now();
        return postImage;
    }

    public void delete() {
        this.deleted = true;
    }
}
