package consome.domain.post;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class PostCategoryTest {

    @Test
    void 카테고리_생성_성공_tester() {
        // given
        Long boardId = 1L;
        String name = "질문";
        int order = 1;

        // when
        PostCategory category = PostCategory.create(boardId, name, order);

        // then
        assertThat(category.getBoardId()).isEqualTo(boardId);
        assertThat(category.getName()).isEqualTo(name);
        assertThat(category.getDisplayOrder()).isEqualTo(order);
        assertThat(category.isDeleted()).isFalse();
        assertThat(category.getCreatedAt()).isNotNull();
        assertThat(category.getUpdatedAt()).isNotNull();
    }

    @Test
    void 카테고리_이름_수정_성공_tester() {
        PostCategory category = PostCategory.create(1L, "질문", 1);

        category.rename("정보");

        assertThat(category.getName()).isEqualTo("정보");
    }

    @Test
    void 카테고리_정렬순서_변경_성공_tester() {
        PostCategory category = PostCategory.create(1L, "질문", 1);

        category.changeOrder(2);

        assertThat(category.getDisplayOrder()).isEqualTo(2);
    }

    @Test
    void 카테고리_삭제_성공_tester() {
        PostCategory category = PostCategory.create(1L, "질문", 1);

        category.delete();

        assertThat(category.isDeleted()).isTrue();
    }
}