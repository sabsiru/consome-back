package consome.domain.admin;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private CategoryService categoryService;

    @Test
    void 유효한_보드ID와_이름과_순서로_생성할_때_저장된_카테고리를_반환한다() {
        // given
        Long categoryId = 10L;
        String name = "롤";
        int order = 2;
        Category saved = Category.create(categoryId, name, order);
        when(categoryRepository.save(any(Category.class))).thenReturn(saved);

        // when
        Category result = categoryService.create(categoryId, name, order);

        // then
        assertThat(result).isSameAs(saved);
        verify(categoryRepository).save(argThat(cat ->
                cat.getBoardId().equals(categoryId) &&
                        cat.getName().equals(name) &&
                        cat.getDisplayOrder() == order
        ));
    }

    @Test
    void 기존_카테고리ID와_새_이름으로_변경할_때_이름이_수정된다() {
        // given
        Long id = 1L;
        Category existing = Category.create(5L, "롤", 1);
        when(categoryRepository.findById(id)).thenReturn(Optional.of(existing));
        when(categoryRepository.save(existing)).thenReturn(existing);

        // when
        Category result = categoryService.rename(id, "피파");

        // then
        assertThat(result.getName()).isEqualTo("피파");
        verify(categoryRepository).findById(id);
        verify(categoryRepository).save(existing);
    }

    @Test
    void 기존_카테고리ID와_새_순서로_변경할_때_displayOrder가_업데이트된다() {
        // given
        Long id = 2L;
        Category existing = Category.create(5L, "롤", 1);
        when(categoryRepository.findById(id)).thenReturn(Optional.of(existing));
        when(categoryRepository.save(existing)).thenReturn(existing);

        // when
        Category result = categoryService.changeOrder(id, 3);

        // then
        assertThat(result.getDisplayOrder()).isEqualTo(3);
        verify(categoryRepository).findById(id);
        verify(categoryRepository).save(existing);
    }

    @Test
    void 기존_카테고리ID로_삭제할_때_deleted가_true가_된다() {
        // given
        Long id = 3L;
        Category existing = Category.create(5L, "롤", 1);
        when(categoryRepository.findById(id)).thenReturn(Optional.of(existing));

        // when
        categoryService.delete(id);

        // then
        assertThat(existing.isDeleted()).isTrue();
        verify(categoryRepository).findById(id);
        verify(categoryRepository).save(existing);
    }

    @Test
    void 유효한_카테고리ID로_조회할_때_카테고리를_반환한다() {
        // given
        Long id = 4L;
        Category existing = Category.create(5L, "피파", 2);
        when(categoryRepository.findById(id)).thenReturn(Optional.of(existing));

        // when
        Category result = categoryService.findById(id);

        // then
        assertThat(result).isSameAs(existing);
        verify(categoryRepository).findById(id);
    }

    @Test
    void 존재하지않는_카테고리ID로_조회할_때_예외를_던진다() {
        // given
        Long id = 99L;
        when(categoryRepository.findById(id)).thenReturn(Optional.empty());

        // when / then
        assertThatThrownBy(() -> categoryService.findById(id))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("잘못된 접근입니다.");
        verify(categoryRepository).findById(id);
    }
}