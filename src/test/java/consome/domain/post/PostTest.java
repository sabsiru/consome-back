package consome.domain.post;

import consome.domain.post.entity.Post;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class PostTest {

    @Test
    void 게시글_생성_성공_tester() {
        // given
        Long boardId = 1L;
        Long categoryId = 2L;
        Long userId = 3L;
        String title = "첫 글";
        String content = "내용입니다";

        // when
        Post post = Post.write(boardId, categoryId, userId, title, content);

        // then
        assertThat(post.getRefBoardId()).isEqualTo(boardId);
        assertThat(post.getRefCategoryId()).isEqualTo(categoryId);
        assertThat(post.getRefUserId()).isEqualTo(userId);
        assertThat(post.getTitle()).isEqualTo(title);
        assertThat(post.getContent()).isEqualTo(content);
        assertThat(post.getCreatedAt()).isNotNull();
        assertThat(post.getUpdatedAt()).isNotNull();
    }

    @Test
    void 게시글_수정_성공_tester() {
        Post post = Post.write(1L, 2L, 3L, "제목", "본문");
        post.edit("수정된 제목", "수정된 내용");

        assertThat(post.getTitle()).isEqualTo("수정된 제목");
        assertThat(post.getContent()).isEqualTo("수정된 내용");
    }

    @Test
    void 작성자_확인_성공_tester() {
        Post post = Post.write(1L, 2L, 100L, "제목", "내용");

        assertThat(post.isAuthor(100L)).isTrue();
        assertThat(post.isAuthor(101L)).isFalse();
    }

    @Test
    void 게시글_삭제_성공_tester() {
        // given
        Post post = Post.write(1L, 2L, 3L, "제목", "본문");

        // when
        post.delete();

        // then
        assertThat(post.isDeleted()).isTrue();
    }
}