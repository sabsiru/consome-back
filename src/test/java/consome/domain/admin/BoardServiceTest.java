package consome.domain.admin;

import consome.domain.admin.repository.BoardRepository;
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
        String name = "자유게시판";
        String description = "자유로운 이야기를 나눠요";
        int order = 1;
        Board saved = Board.create(name, description, order);
        when(boardRepository.save(any(Board.class))).thenReturn(saved);

        // when
        Board result = boardService.create(name, description, order);

        // then
        assertThat(result).isSameAs(saved);
        verify(boardRepository).save(argThat(b ->
                        b.getName().equals(name) &&
                        b.getDescription().equals(description) &&
                        b.getDisplayOrder() == order
        ));
    }


    @Test
    void 기존_보드ID와_새_순서로_변경할_때_displayOrder가_업데이트된다() {
        // given
        Long id = 2L;
        Board existing = Board.create("자유게시판", "설명", 1);
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
        Board existing = Board.create( "자유게시판", "설명", 1);
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
        Board existing = Board.create("자유게시판", "설명", 1);
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

    @Test
    void update_이름만_수정할_때_name이_변경된다() {
        // given
        Long id = 1L;
        Board existing = Board.create( "자유게시판", "설명", 1);
        when(boardRepository.findById(id)).thenReturn(Optional.of(existing));
        when(boardRepository.save(existing)).thenReturn(existing);

        // when
        Board result = boardService.update(id, "정보게시판", null);

        // then
        assertThat(result.getName()).isEqualTo("정보게시판");
        assertThat(result.getDescription()).isEqualTo("설명");
    }

    @Test
    void update_설명만_수정할_때_description이_변경된다() {
        // given
        Long id = 1L;
        Board existing = Board.create("자유게시판", "설명", 1);
        when(boardRepository.findById(id)).thenReturn(Optional.of(existing));
        when(boardRepository.save(existing)).thenReturn(existing);

        // when
        Board result = boardService.update(id, null, "새로운 설명");

        // then
        assertThat(result.getName()).isEqualTo("자유게시판");
        assertThat(result.getDescription()).isEqualTo("새로운 설명");
    }

    @Test
    void update_중복된_이름으로_수정할_때_예외를_던진다() {
        // given
        Long id = 1L;
        String duplicateName = "중복된게시판";
        Board existing = Board.create("자유게시판", "설명", 1);

        when(boardRepository.findById(id)).thenReturn(Optional.of(existing));
        when(boardRepository.existsByName(duplicateName)).thenReturn(true);

        // when / then
        assertThatThrownBy(() -> boardService.update(id, duplicateName, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("이미 존재하는 게시판 이름입니다.");
    }

}