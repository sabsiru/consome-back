package consome.domain.board;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class CategoryTest {

    @Test
    void 카테고리_생성_성공_tester() {
        // given
        Long categoryId = 1L;
        String name = "LOL";
        int order = 2;

        // when
        Category category = Category.create(categoryId, name, order);

        // then
        assertThat(category.getBoardId()).isEqualTo(categoryId);
        assertThat(category.getName()).isEqualTo(name);
        assertThat(category.getDisplayOrder()).isEqualTo(order);
        assertThat(category.isDeleted()).isFalse();
    }

    @Test
    void 카테고리_이름_변경_성공_tester() {
        Category category = Category.create(1L, "LOL", 1);

        category.rename("스타");

        assertThat(category.getName()).isEqualTo("스타");
    }

    @Test
    void 카테고리_정렬순서_변경_성공_tester() {
        Category category = Category.create(1L, "LOL", 1);

        category.changeOrder(3);

        assertThat(category.getDisplayOrder()).isEqualTo(3);
    }

    @Test
    void 카테고리_삭제_성공_tester() {
        Category category = Category.create(1L, "LOL", 1);

        category.delete();

        assertThat(category.isDeleted()).isTrue();
    }
}