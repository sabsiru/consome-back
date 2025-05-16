package consome.domain.comment;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CommentLikeTest {

    @Test
    void 댓글_좋아요_생성_성공_tester() {
        // given
        Long commentId = 1L;
        Long userId = 100L;

        // when
        CommentLike like = CommentLike.create(commentId, userId);

        // then
        assertThat(like.getCommentId()).isEqualTo(commentId);
        assertThat(like.getUserId()).isEqualTo(userId);
        assertThat(like.isDeleted()).isFalse();
        assertThat(like.getCreatedAt()).isNotNull();
        assertThat(like.getUpdatedAt()).isNotNull();
    }

    @Test
    void 댓글_좋아요_취소_성공_tester() {
        CommentLike like = CommentLike.create(1L, 100L);

        like.cancel();

        assertThat(like.isDeleted()).isTrue();
    }

    @Test
    void 댓글_좋아요_복원_성공_tester() {
        CommentLike like = CommentLike.create(1L, 100L);
        like.cancel();

        like.restore();

        assertThat(like.isDeleted()).isFalse();
    }
}