package consome.domain.board;

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
class SectionServiceTest {

    @Mock
    private SectionRepository sectionRepository;

    @InjectMocks
    private SectionService sectionService;

    @Test
    void 유효한_이름과_순서로_생성할_때_저장된_섹션을_반환한다() {
        // given
        String name = "게임";
        int order = 1;
        Section saved = Section.create(name, order);
        when(sectionRepository.save(any(Section.class))).thenReturn(saved);

        // when
        Section result = sectionService.create(name, order);

        // then
        assertThat(result).isSameAs(saved);
        verify(sectionRepository).save(argThat(sec ->
                sec.getName().equals(name) &&
                        sec.getDisplayOrder() == order
        ));
    }

    @Test
    void 기존_섹션_ID와_새_이름으로_변경할_때_이름이_수정된다() {
        // given
        Long id = 1L;
        Section existing = Section.create("게임", 1);
        when(sectionRepository.findById(id)).thenReturn(Optional.of(existing));
        when(sectionRepository.save(existing)).thenReturn(existing);

        // when
        Section result = sectionService.rename(id, "디지털");

        // then
        assertThat(result.getName()).isEqualTo("디지털");
        verify(sectionRepository).findById(id);
        verify(sectionRepository).save(existing);
    }

    @Test
    void 기존_섹션_ID와_새_순서로_변경할_때_displayOrder가_업데이트된다() {
        // given
        Long id = 2L;
        Section existing = Section.create("게임", 1);
        when(sectionRepository.findById(id)).thenReturn(Optional.of(existing));
        when(sectionRepository.save(existing)).thenReturn(existing);

        // when
        Section result = sectionService.changeOrder(id, 5);

        // then
        assertThat(result.getDisplayOrder()).isEqualTo(5);
        verify(sectionRepository).findById(id);
        verify(sectionRepository).save(existing);
    }

    @Test
    void 기존_섹션_ID로_삭제할_때_deleted가_true가_된다() {
        // given
        Long id = 3L;
        Section existing = Section.create("게임", 1);
        when(sectionRepository.findById(id)).thenReturn(Optional.of(existing));

        // when
        sectionService.delete(id);

        // then
        assertThat(existing.isDeleted()).isTrue();
        verify(sectionRepository).findById(id);
        verify(sectionRepository).save(existing);
    }

    @Test
    void 유효한_ID로_조회할_때_섹션을_반환한다() {
        // given
        Long id = 4L;
        Section existing = Section.create("디지털", 2);
        when(sectionRepository.findById(id)).thenReturn(Optional.of(existing));

        // when
        Section result = sectionService.findById(id);

        // then
        assertThat(result).isSameAs(existing);
        verify(sectionRepository).findById(id);
    }

    @Test
    void 존재하지않는_ID로_조회할_때_IllegalArgumentException을_던진다() {
        // given
        Long id = 99L;
        when(sectionRepository.findById(id)).thenReturn(Optional.empty());

        // when / then
        assertThatThrownBy(() -> sectionService.findById(id))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("잘못된 접근입니다.");
        verify(sectionRepository).findById(id);
    }
}