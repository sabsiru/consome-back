package consome.domain.post;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Collection;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long boardId;

    @Column(nullable = false)
    private Long categoryId;

    @Column(nullable = false)
    private Long authorId; // userId

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

    private Post(Long boardId, Long categoryId, Long authorId, String title, String content) {
        this.boardId = boardId;
        this.categoryId = categoryId;
        this.authorId = authorId;
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
        return this.authorId.equals(userId);
    }
}