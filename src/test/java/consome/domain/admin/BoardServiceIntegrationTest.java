package consome.domain.admin;


import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class BoardServiceIntegrationTest {

    @Autowired
    BoardService boardService;

    @Autowired
    BoardRepository boardRepository;

    @Test
    void 게시판_생성시_DB에_저장된다() {
        //given
        String name = "자유게시판";
        String description = "자유롭게 글을 작성할 수 있는 게시판입니다.";
        int displayOrder = 1;

        //when
        Board board = boardService.create(name, description, displayOrder);

        //then
        assertThat(board.getId()).isNotNull();
        assertThat(board.getName()).isEqualTo(name);
        assertThat(board.getDescription()).isEqualTo(description);
        assertThat(board.getDisplayOrder()).isEqualTo(displayOrder);
        assertThat(board.isDeleted()).isFalse();
    }

    @Test
    void 게시판_이름_변경시_DB에_저장된다() {
        //given
        String name = "자유게시판";
        String description = "자유롭게 글을 작성할 수 있는 게시판입니다.";
        int displayOrder = 1;
        Board board = boardService.create(name, description, displayOrder);

        //when
        String newName = "새로운 자유게시판";
        Board renamedBoard = boardService.update(board.getId(), newName, null);

        //then
        assertThat(renamedBoard.getName()).isEqualTo(newName);
    }

    @Test
    void 게시판_정렬순서_변경시_DB에_저장된다() {
        //given
        String name = "자유게시판";
        String description = "자유롭게 글을 작성할 수 있는 게시판입니다.";
        int displayOrder = 1;
        Board board = boardService.create(name, description, displayOrder);

        //when
        int newOrder = 2;
        Board orderedBoard = boardService.changeOrder(board.getId(), newOrder);

        //then
        assertThat(orderedBoard.getDisplayOrder()).isEqualTo(newOrder);
    }

    @Test
    void 게시판_삭제시_isDeleted가_true로_변경된다() {
        //given
        String name = "자유게시판";
        String description = "자유롭게 글을 작성할 수 있는 게시판입니다.";
        int displayOrder = 1;
        Board board = boardService.create(name, description, displayOrder);

        //when
        boardService.delete(board.getId());

        //then
        Board deletedBoard = boardRepository.findById(board.getId()).orElseThrow();
        assertThat(deletedBoard.isDeleted()).isTrue();
    }

    @Test
    void 중복된_게시판_이름은_예외발생() {
        //given
        String name = "중복게시판";
        String description = "첫 번째 게시판";
        int displayOrder = 1;
        boardService.create(name, description, displayOrder);

        //when & then
        assertThatThrownBy(() -> boardService.create(name, "두 번째 게시판", 2))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("이미 존재하는 게시판 이름입니다.");
    }

    @Test
    void 게시판_이름_수정시_중복되면_예외발생() {
        //given
        String name = "게시판1";
        String description = "첫 번째 게시판";
        int displayOrder = 1;
        Board board1 = boardService.create(name, description, displayOrder);

        String name2 = "게시판2";
        boardService.create(name2, "두 번째 게시판", 2);

        //when & then
        assertThatThrownBy(() -> boardService.update(board1.getId(), name2, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("이미 존재하는 게시판 이름입니다.");
    }

    @Test
    void 게시판_이름_유효성_검사() {
        //given
        String invalidName = ""; // 빈 문자열
        String description = "유효성 검사 게시판";
        int displayOrder = 1;

        //when & then
        assertThatThrownBy(() -> boardService.create(invalidName, description, displayOrder))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("게시판 이름은 1자 이상 10자 이하로 입력해야 합니다.");
    }
}
