// src/test/java/consome/domain/post/PostStatTest.java
package consome.domain.post;

import consome.domain.post.entity.PostStat;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class PostStatTest {

    @Test
    void 조회수_증가_테스트() {
        PostStat stat = new PostStat();
        stat.increaseViewCount();
        assertThat(stat.getViewCount()).isEqualTo(1);
    }

    @Test
    void 좋아요_증가_감소_테스트() {
        PostStat stat = new PostStat();
        stat.increaseLikeCount();
        assertThat(stat.getLikeCount()).isEqualTo(1);

        stat.decreaseLikeCount();
        assertThat(stat.getLikeCount()).isEqualTo(0);

        stat.decreaseLikeCount();
        assertThat(stat.getLikeCount()).isEqualTo(0);
    }

    @Test
    void 싫어요_증가_감소_테스트() {
        PostStat stat = new PostStat();
        stat.increaseDislikeCount();
        assertThat(stat.getDislikeCount()).isEqualTo(1);

        stat.decreaseDislikeCount();
        assertThat(stat.getDislikeCount()).isEqualTo(0);

        stat.decreaseDislikeCount();
        assertThat(stat.getDislikeCount()).isEqualTo(0);
    }

    @Test
    void 댓글수_증가_감소_테스트() {
        PostStat stat = new PostStat();
        stat.increaseCommentCount();
        assertThat(stat.getCommentCount()).isEqualTo(1);

        stat.decreaseCommentCount();
        assertThat(stat.getCommentCount()).isEqualTo(0);

        stat.decreaseCommentCount();
        assertThat(stat.getCommentCount()).isEqualTo(0);
    }
}