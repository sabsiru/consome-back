package consome.domain.admin;

import consome.domain.admin.repository.CategoryRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class CategoryServiceIntegrationTest {

    @Autowired
    CategoryService categoryService;

    @Autowired
    CategoryRepository categoryRepository;

    @Test
    void 카테고리_생성시_DB에_저장된다() {
        //given
        Long categoryId = 1L;
        String name = "게임";
        int displayOrder = 1;

        //when
        Category category = categoryService.create(categoryId, name, displayOrder);

        //then
        assertThat(category.getId()).isNotNull();
        assertThat(category.getBoardId()).isEqualTo(categoryId);
        assertThat(category.getName()).isEqualTo(name);
        assertThat(category.getDisplayOrder()).isEqualTo(displayOrder);
        assertThat(category.isDeleted()).isFalse();
    }

    @Test
    void 카테고리를_삭제하면_isDelted가_true로_변경된다(){
        //given
        Category category = categoryService.create(1L, "게임", 1);

        //when
        categoryService.delete(category.getId());

        //then
        Category deletedCategory = categoryRepository.findById(category.getId()).orElseThrow();
        assertThat(deletedCategory.isDeleted()).isTrue();
    }

    @Test
    void 존재하지_않는_ID로_삭제하면_예외가_발생한다() {
        // expect
        assertThatThrownBy(() -> categoryService.delete(999L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("잘못된 접근");
    }

    @Test
    void 중복된_name은_예외발생() {
        //given
        Long categoryId = 1L;
        String name = "중복";
        int displayOrder = 1;
        categoryService.create(categoryId, name, displayOrder);

        //when & then
        assertThatThrownBy(() -> categoryService.create(categoryId, name, displayOrder))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("이미 존재하는 카테고리 이름입니다.");
    }

    @Test
    void 카테고리_이름_변경_성공() {
        //given
        Long categoryId = 1L;
        String name = "게임";
        Long boardId = 1L;
        int displayOrder = 1;
        Category category = categoryService.create(categoryId, name, displayOrder);

        //when
        String newName = "스포츠";
        Category renamedCategory = categoryService.rename(category.getId(), newName, boardId);

        //then
        assertThat(renamedCategory.getName()).isEqualTo(newName);
    }

    @Test
    void name_을_수정시_중복되면_예외발생() {
        //given
        Long categoryId = 1L;
        String name = "카테고리1";
        Long boardId = 1L;
        int displayOrder = 1;
        Category category1 = categoryService.create(categoryId, name, displayOrder);

        String duplicateName = "카테고리2";
        categoryService.create(categoryId, duplicateName, displayOrder + 1);

        //when & then
        assertThatThrownBy(() -> categoryService.rename(category1.getId(), duplicateName, boardId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("이미 존재하는 카테고리 이름입니다.");
    }

    @Test
    void 카테고리_정렬순서_변경_성공() {
        // given
        Long categoryId = 1L;
        String name = "카테고리1";
        int originalOrder = 1;
        Category category = categoryService.create(categoryId, name, originalOrder);

        // when
        int newOrder = 2;
        Category updatedCategory = categoryService.changeOrder(category.getId(), newOrder);

        // then
        assertThat(updatedCategory.getDisplayOrder()).isEqualTo(newOrder);
    }

    @Test
    void 카테고리_이름_유효성_검사() {
        // when & then
        assertThatThrownBy(() -> categoryService.create(1L, "", 1))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("카테고리 이름은 1자 이상 10자 이하로 입력해야 합니다.");

        assertThatThrownBy(() -> categoryService.create(1L, " ", 1))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("카테고리 이름은 1자 이상 10자 이하로 입력해야 합니다.");

        assertThatThrownBy(() -> categoryService.create(1L, "12345678901", 1))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("카테고리 이름은 1자 이상 10자 이하로 입력해야 합니다.");
    }
}
