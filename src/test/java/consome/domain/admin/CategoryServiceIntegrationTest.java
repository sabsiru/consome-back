package consome.domain.admin;

import consome.domain.admin.repository.CategoryRepository;
import consome.domain.common.exception.BusinessException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.utility.TestcontainersConfiguration;

import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

@SpringBootTest
@Import(TestcontainersConfiguration.class)
@Transactional
@ActiveProfiles("test")
public class CategoryServiceIntegrationTest {

    @Autowired
    CategoryService categoryService;

    @Autowired
    CategoryRepository categoryRepository;

    private String uniqueName() {
        return "카테" + UUID.randomUUID().toString().substring(0, 4);
    }

    private int uniqueOrder() {
        return (int) (System.nanoTime() % 100000);
    }

    @Test
    void 카테고리_생성시_DB에_저장된다() {
        //given
        Long boardId = 1L;
        String name = uniqueName();
        int displayOrder = uniqueOrder();

        //when
        Category category = categoryService.create(boardId, name, displayOrder);

        //then
        assertThat(category.getId()).isNotNull();
        assertThat(category.getBoardId()).isEqualTo(boardId);
        assertThat(category.getName()).isEqualTo(name);
        assertThat(category.getDisplayOrder()).isEqualTo(displayOrder);
        assertThat(category.isDeleted()).isFalse();
    }

    @Test
    void 카테고리를_삭제하면_isDelted가_true로_변경된다(){
        //given
        Category category = categoryService.create(1L, uniqueName(), uniqueOrder());

        //when
        categoryService.delete(category.getId());

        //then
        Category deletedCategory = categoryRepository.findById(category.getId()).orElseThrow();
        assertThat(deletedCategory.isDeleted()).isTrue();
    }

    @Test
    void 존재하지_않는_ID로_삭제하면_예외가_발생한다() {
        // expect
        assertThatThrownBy(() -> categoryService.delete(999999L))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("카테고리를 찾을 수 없습니다");
    }

    @Test
    void 중복된_name은_예외발생() {
        //given
        Long boardId = 1L;
        String name = uniqueName();
        categoryService.create(boardId, name, uniqueOrder());

        //when & then
        assertThatThrownBy(() -> categoryService.create(boardId, name, uniqueOrder()))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("이미 존재하는 카테고리 이름입니다.");
    }

    @Test
    void 카테고리_이름_변경_성공() {
        //given
        Long boardId = 1L;
        String name = uniqueName();
        Category category = categoryService.create(boardId, name, uniqueOrder());

        //when
        String newName = uniqueName();
        Category renamedCategory = categoryService.rename(category.getId(), newName, boardId);

        //then
        assertThat(renamedCategory.getName()).isEqualTo(newName);
    }

    @Test
    void name_을_수정시_중복되면_예외발생() {
        //given
        Long boardId = 1L;
        String name = uniqueName();
        Category category1 = categoryService.create(boardId, name, uniqueOrder());

        String duplicateName = uniqueName();
        categoryService.create(boardId, duplicateName, uniqueOrder());

        //when & then
        assertThatThrownBy(() -> categoryService.rename(category1.getId(), duplicateName, boardId))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("이미 존재하는 카테고리 이름입니다.");
    }

    @Test
    void 카테고리_정렬순서_변경_성공() {
        // given
        Long boardId = 1L;
        String name = uniqueName();
        int originalOrder = uniqueOrder();
        Category category = categoryService.create(boardId, name, originalOrder);

        // when
        int newOrder = uniqueOrder();
        Category updatedCategory = categoryService.changeOrder(category.getId(), newOrder);

        // then
        assertThat(updatedCategory.getDisplayOrder()).isEqualTo(newOrder);
    }

    @Test
    void 카테고리_이름_유효성_검사() {
        // when & then
        assertThatThrownBy(() -> categoryService.create(1L, "", uniqueOrder()))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("카테고리 이름은 1자 이상 10자 이하로 입력해야 합니다.");

        assertThatThrownBy(() -> categoryService.create(1L, " ", uniqueOrder()))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("카테고리 이름은 1자 이상 10자 이하로 입력해야 합니다.");

        assertThatThrownBy(() -> categoryService.create(1L, "12345678901", uniqueOrder()))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("카테고리 이름은 1자 이상 10자 이하로 입력해야 합니다.");
    }
}
