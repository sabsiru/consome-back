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

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long postId;

    @Column(nullable = false)
    private Long userId;

    @Column
    private Long parentId;

    @Column(nullable = false)
    private int ref = 0;

    @Column(nullable = false)
    private int step = 0;

    @Column(nullable = false)
    private int depth = 0;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @Column(nullable = false)
    private boolean deleted = false;

    public Comment(Long postId, Long userId, Long parentId,
                   int ref, int step, int depth, String content) {
        this.postId = postId;
        this.userId = userId;
        this.parentId = parentId;
        this.ref = ref;
        this.step = step;
        this.depth = depth;
        this.content = content;
        this.createdAt = LocalDateTime.now();
    }

    @PostPersist
    public void setRefAfterPersist() {
        if (this.parentId == null) {
            this.ref = this.id.intValue();
        }
    }

    public static Comment reply(Long postId, Long userId, Comment parent, String content, int step) {
        if (parent == null) {
            return new Comment(postId, userId, null, 0, step, 0, content);
        } else {
            return new Comment(postId, userId, parent.getId(), parent.getRef(),
                    step + 1, parent.getDepth() + 1, content);
        }
    }

    public void edit(String content) {
        this.content = content;
        this.updatedAt = LocalDateTime.now();
    }

    public void delete() {
        this.deleted = true;
        this.updatedAt = LocalDateTime.now();
    }


}