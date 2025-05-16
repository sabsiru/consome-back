package consome.domain.comment;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Comment {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long postId;

    @Column(nullable = false)
    private Long userId;

    @Column
    private Long parentId;

    @Column(nullable = false)
    private String groupPath;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @Column(nullable = false)
    private boolean deleted = false;

    private Comment(Long postId, Long userId, Long parentId, String groupPath, String content) {
        this.postId = postId;
        this.userId = userId;
        this.parentId = parentId;
        this.groupPath = groupPath;
        this.content = content;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public static Comment create(Long postId, Long userId, Long parentId, String groupPath, String content) {
        return new Comment(postId, userId, parentId, groupPath, content);
    }

    public void delete() {
        this.deleted = true;
        this.updatedAt = LocalDateTime.now();
    }

    public boolean isRoot() {
        return this.parentId == null;
    }
}