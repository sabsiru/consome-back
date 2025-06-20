package consome.domain.comment;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CommentTest {

    @Test
    void 댓글_생성_테스트() {
        // given
        Long postId = 1L;
        Long userId = 1L;
        String content = "댓글 내용";

        // when
        Comment comment = new Comment(postId, userId, null, 0, 0, 0, content);

        // then
        assertThat(comment.getPostId()).isEqualTo(postId);
        assertThat(comment.getUserId()).isEqualTo(userId);
        assertThat(comment.getParentId()).isNull();
        assertThat(comment.getRef()).isEqualTo(0);
        assertThat(comment.getStep()).isEqualTo(0);
        assertThat(comment.getDepth()).isEqualTo(0);
        assertThat(comment.getContent()).isEqualTo(content);
        assertThat(comment.isDeleted()).isFalse();
        assertThat(comment.getCreatedAt()).isNotNull();
    }

    @Test
    void 대댓글_생성_테스트() {
        // given
        Long postId = 1L;
        Long userId = 2L;
        Comment parentComment = new Comment(postId, 1L, null, 1, 0, 0, "부모 댓글");

        // when
        Comment replyComment = Comment.reply(postId, userId, parentComment, "대댓글 내용", 1);

        // then
        assertThat(replyComment.getPostId()).isEqualTo(postId);
        assertThat(replyComment.getUserId()).isEqualTo(userId);
        assertThat(replyComment.getParentId()).isEqualTo(parentComment.getId());
        assertThat(replyComment.getRef()).isEqualTo(parentComment.getRef());
        assertThat(replyComment.getStep()).isEqualTo(2);
        assertThat(replyComment.getDepth()).isEqualTo(1);
        assertThat(replyComment.getContent()).isEqualTo("대댓글 내용");
    }

    @Test
    void 댓글_삭제_테스트() {
        // given
        Comment comment = new Comment(1L, 1L, null, 0, 0, 0, "댓글 내용");

        // when
        comment.delete();

        // then
        assertThat(comment.isDeleted()).isTrue();
        assertThat(comment.getUpdatedAt()).isNotNull();
    }
}