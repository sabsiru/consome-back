package consome.domain.post.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TempPostVideo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

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

    public static TempPostVideo create(String url, String storedName,
                                       String originalName, Long fileSize) {
        TempPostVideo temp = new TempPostVideo();
        temp.url = url;
        temp.storedName = storedName;
        temp.originalName = originalName;
        temp.fileSize = fileSize;
        temp.createdAt = LocalDateTime.now();
        return temp;
    }

    public PostVideo toPostVideo(Long postId) {
        return PostVideo.create(postId, this.url, this.storedName,
                this.originalName, this.fileSize);
    }
}
