package consome.domain.comment;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class CommentTest {

    @Test
    void 댓글_생성_성공_tester() {
        // given
        Long postId = 1L;
        Long userId = 2L;
        Long parentId = null;
        String groupPath = "001";
        String content = "첫 댓글입니다.";

        // when
        Comment comment = Comment.create(postId, userId, parentId, groupPath, content);

        // then
        assertThat(comment.getPostId()).isEqualTo(postId);
        assertThat(comment.getUserId()).isEqualTo(userId);
        assertThat(comment.getParentId()).isNull();
        assertThat(comment.getGroupPath()).isEqualTo(groupPath);
        assertThat(comment.getContent()).isEqualTo(content);
        assertThat(comment.getCreatedAt()).isNotNull();
        assertThat(comment.getUpdatedAt()).isNotNull();
    }

    @Test
    void 대댓글_생성_성공_tester() {
        Comment reply = Comment.create(1L, 2L, 10L, "001/003", "대댓글입니다.");
        assertThat(reply.getParentId()).isEqualTo(10L);
    }

    @Test
    void 루트댓글_판별_성공_tester() {
        Comment root = Comment.create(1L, 2L, null, "001", "본문 댓글");
        Comment reply = Comment.create(1L, 2L, 3L, "001/003", "대댓글");

        assertThat(root.isRoot()).isTrue();
        assertThat(reply.isRoot()).isFalse();
    }
}