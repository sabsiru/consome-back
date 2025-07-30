package consome.domain.post.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long refBoardId;

    @Column(nullable = false)
    private Long refCategoryId;

    @Column(nullable = false)
    private Long refUserId;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @Column(nullable = false)
    private boolean deleted = false;

    private Post(Long refBoardId, Long refCategoryId, Long refUserId, String title, String content) {
        this.refBoardId = refBoardId;
        this.refCategoryId = refCategoryId;
        this.refUserId = refUserId;
        this.title = title;
        this.content = content;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public static Post write(Long boardId, Long categoryId, Long authorId, String title, String content) {
        return new Post(boardId, categoryId, authorId, title, content);
    }


    public void delete() {
        this.deleted = true;
        this.updatedAt = LocalDateTime.now();
    }

    public void edit(String title, String content) {
        this.title = title;
        this.content = content;
        this.updatedAt = LocalDateTime.now();
    }

    public boolean isAuthor(Long userId) {
        return this.refUserId.equals(userId);
    }
}