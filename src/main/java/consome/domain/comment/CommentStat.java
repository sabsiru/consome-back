package consome.domain.comment;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CommentStat {

    @Id
    private Long commentId;

    @MapsId
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "comment_id")
    private Comment comment;

    @Column(nullable = false)
    private int likeCount = 0;

    @Column(nullable = false)
    private int dislikeCount = 0;

    public static CommentStat init(Comment comment) {
        CommentStat stat = new CommentStat();
        stat.comment = comment;
        stat.commentId = comment.getId();
        return stat;
    }

    public void increaseLikeCount() {
        this.likeCount++;
    }

    public void decreaseLikeCount() {
        if (this.likeCount > 0) this.likeCount--;
    }

    public void increaseDislikeCount() {
        this.dislikeCount++;
    }

    public void decreaseDislikeCount() {
        if (this.dislikeCount > 0) this.dislikeCount--;
    }
}
