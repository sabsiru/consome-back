package consome.domain.post.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Where;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Where(clause = "deleted = false")
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long boardId;

    @Column(nullable = false)
    private Long categoryId;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, columnDefinition = "MEDIUMTEXT")
    private String content;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @Column(nullable = false)
    private boolean deleted = false;

    private Post(Long boardId, Long categoryId, Long userId, String title, String content) {
        this.boardId = boardId;
        this.categoryId = categoryId;
        this.userId = userId;
        this.title = title;
        this.content = content;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public static Post post(Long boardId, Long categoryId, Long authorId, String title, String content) {
        //검증 로직
        return new Post(boardId, categoryId, authorId, title, content);
    }

    /**
     * to-do
     * 검증 로직 넣을것*/

    public void delete() {
        this.deleted = true;
        this.updatedAt = LocalDateTime.now();
    }

    public void edit(String title, Long categoryId, String content) {
        this.title = title;
        this.categoryId = categoryId;
        this.content = content;
        this.updatedAt = LocalDateTime.now();
    }

    public boolean isAuthor(Long userId) {
        return this.userId.equals(userId);
    }
}