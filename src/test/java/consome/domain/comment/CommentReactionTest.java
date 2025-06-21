package consome.domain.comment;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CommentReactionTest {

    private Long commentId = 1L;
    private Long userId = 100L;

    @Test
    void 댓글_좋아요_생성_성공() {
        // when
        CommentReaction like = CommentReaction.like(commentId, userId);

        // then
        assertThat(like.getCommentId()).isEqualTo(commentId);
        assertThat(like.getUserId()).isEqualTo(userId);
        assertThat(like.getCreatedAt()).isNotNull();
    }

    @Test
    void 댓글_싫어요_생성_성공(){
        //when
        CommentReaction dislike = CommentReaction.dislike(commentId, userId);

        //then
        assertThat(dislike.getCommentId()).isEqualTo(commentId);
        assertThat(dislike.getUserId()).isEqualTo(userId);
    }



}