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
class BoardServiceTest {

    @Mock
    private BoardRepository boardRepository;

    @InjectMocks
    private BoardService boardService;

    @Test
    void 유효한_섹션ID와_이름과_설명과_순서로_생성할_때_저장된_보드를_반환한다() {
        // given
        Long sectionId = 5L;
        String name = "자유게시판";
        String description = "자유로운 이야기를 나눠요";
        int order = 1;
        Board saved = Board.create(sectionId, name, description, order);
        when(boardRepository.save(any(Board.class))).thenReturn(saved);

        // when
        Board result = boardService.create(sectionId, name, description, order);

        // then
        assertThat(result).isSameAs(saved);
        verify(boardRepository).save(argThat(b ->
                b.getSectionId().equals(sectionId) &&
                        b.getName().equals(name) &&
                        b.getDescription().equals(description) &&
                        b.getDisplayOrder() == order
        ));
    }

    @Test
    void 기존_보드ID와_새_이름으로_변경할_때_name이_수정된다() {
        // given
        Long id = 1L;
        Board existing = Board.create(5L, "자유게시판", "설명", 1);
        when(boardRepository.findById(id)).thenReturn(Optional.of(existing));
        when(boardRepository.save(existing)).thenReturn(existing);

        // when
        Board result = boardService.rename(id, "정보게시판");

        // then
        assertThat(result.getName()).isEqualTo("정보게시판");
        verify(boardRepository).findById(id);
        verify(boardRepository).save(existing);
    }

    @Test
    void 기존_보드ID와_새_순서로_변경할_때_displayOrder가_업데이트된다() {
        // given
        Long id = 2L;
        Board existing = Board.create(5L, "자유게시판", "설명", 1);
        when(boardRepository.findById(id)).thenReturn(Optional.of(existing));
        when(boardRepository.save(existing)).thenReturn(existing);

        // when
        Board result = boardService.changeOrder(id, 3);

        // then
        assertThat(result.getDisplayOrder()).isEqualTo(3);
        verify(boardRepository).findById(id);
        verify(boardRepository).save(existing);
    }

    @Test
    void 기존_보드ID로_삭제할_때_deleted가_true가_된다() {
        // given
        Long id = 3L;
        Board existing = Board.create(5L, "자유게시판", "설명", 1);
        when(boardRepository.findById(id)).thenReturn(Optional.of(existing));

        // when
        boardService.delete(id);

        // then
        assertThat(existing.isDeleted()).isTrue();
        verify(boardRepository).findById(id);
        verify(boardRepository).save(existing);
    }

    @Test
    void 유효한_보드ID로_조회할_때_보드를_반환한다() {
        // given
        Long id = 4L;
        Board existing = Board.create(5L, "자유게시판", "설명", 1);
        when(boardRepository.findById(id)).thenReturn(Optional.of(existing));

        // when
        Board result = boardService.findById(id);

        // then
        assertThat(result).isSameAs(existing);
        verify(boardRepository).findById(id);
    }

    @Test
    void 존재하지않는_보드ID로_조회할_때_예외를_던진다() {
        // given
        Long id = 99L;
        when(boardRepository.findById(id)).thenReturn(Optional.empty());

        // when / then
        assertThatThrownBy(() -> boardService.findById(id))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("잘못된 접근입니다.");
        verify(boardRepository).findById(id);
    }
}