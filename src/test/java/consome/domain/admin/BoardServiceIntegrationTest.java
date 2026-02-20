package consome.domain.admin;

import consome.domain.admin.repository.BoardRepository;
import consome.domain.admin.repository.SectionRepository;
import consome.domain.common.exception.BusinessException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.utility.TestcontainersConfiguration;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

@SpringBootTest
@Import(TestcontainersConfiguration.class)
@Transactional
@ActiveProfiles("test")
public class BoardServiceIntegrationTest {

    @Autowired
    BoardService boardService;

    @Autowired
    BoardRepository boardRepository;

    @Autowired
    SectionRepository sectionRepository;

    private Long testSectionId;

    @BeforeEach
    void setUp() {
        Section section = Section.create("테스트섹션" + UUID.randomUUID().toString().substring(0, 4), false);
        testSectionId = sectionRepository.save(section).getId();
    }

    private String uniqueName() {
        return "보드" + UUID.randomUUID().toString().substring(0, 4);
    }

    @Test
    void 게시판_생성시_DB에_저장된다() {
        //given
        String name = uniqueName();
        String description = "자유롭게 글을 작성할 수 있는 게시판입니다.";

        //when
        Board board = boardService.create(name, description, testSectionId);

        //then
        assertThat(board.getId()).isNotNull();
        assertThat(board.getName()).isEqualTo(name);
        assertThat(board.getDescription()).isEqualTo(description);
        assertThat(board.getDisplayOrder()).isEqualTo(0);
        assertThat(board.isDeleted()).isFalse();
    }

    @Test
    void 게시판_이름_변경시_DB에_저장된다() {
        //given
        String name = uniqueName();
        Board board = boardService.create(name, "설명", testSectionId);

        //when
        String newName = uniqueName();
        Board renamedBoard = boardService.update(board.getId(), newName, null);

        //then
        assertThat(renamedBoard.getName()).isEqualTo(newName);
    }

    @Test
    void 게시판_삭제시_isDeleted가_true로_변경된다() {
        //given
        Board board = boardService.create(uniqueName(), "설명", testSectionId);

        //when
        boardService.delete(board.getId());

        //then
        Board deletedBoard = boardRepository.findById(board.getId()).orElseThrow();
        assertThat(deletedBoard.isDeleted()).isTrue();
    }

    @Test
    void 중복된_게시판_이름은_예외발생() {
        //given
        String name = uniqueName();
        boardService.create(name, "첫 번째 게시판", testSectionId);

        //when & then
        assertThatThrownBy(() -> boardService.create(name, "두 번째 게시판", testSectionId))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("이미 존재하는 게시판 이름입니다.");
    }

    @Test
    void 게시판_이름_수정시_중복되면_예외발생() {
        //given
        String name = uniqueName();
        Board board1 = boardService.create(name, "첫 번째 게시판", testSectionId);

        String name2 = uniqueName();
        boardService.create(name2, "두 번째 게시판", testSectionId);

        //when & then
        assertThatThrownBy(() -> boardService.update(board1.getId(), name2, null))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("이미 존재하는 게시판 이름입니다.");
    }

    @Test
    void 게시판_이름_유효성_검사() {
        //given
        String invalidName = "";

        //when & then
        assertThatThrownBy(() -> boardService.create(invalidName, "유효성 검사 게시판", testSectionId))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("게시판 이름은 1자 이상 10자 이하로 입력해야 합니다.");
    }

    @Test
    void 메인게시판_토글시_순서_자동부여() {
        //given
        Board board1 = boardService.create(uniqueName(), "설명1", testSectionId);
        Board board2 = boardService.create(uniqueName(), "설명2", testSectionId);

        //when
        Board toggled1 = boardService.toggleMain(board1.getId());
        Board toggled2 = boardService.toggleMain(board2.getId());

        //then
        assertThat(toggled1.isMain()).isTrue();
        assertThat(toggled1.getDisplayOrder()).isEqualTo(1);
        assertThat(toggled2.isMain()).isTrue();
        assertThat(toggled2.getDisplayOrder()).isEqualTo(2);
    }

    @Test
    void 메인게시판_OFF시_순서_초기화() {
        //given
        Board board = boardService.create(uniqueName(), "설명", testSectionId);
        boardService.toggleMain(board.getId()); // ON

        //when
        Board toggled = boardService.toggleMain(board.getId()); // OFF

        //then
        assertThat(toggled.isMain()).isFalse();
        assertThat(toggled.getDisplayOrder()).isEqualTo(0);
    }

    @Test
    void 메인게시판_목록_조회() {
        //given
        Board board1 = boardService.create(uniqueName(), "설명1", testSectionId);
        Board board2 = boardService.create(uniqueName(), "설명2", testSectionId);
        boardService.toggleMain(board1.getId());

        //when
        var mainBoards = boardService.findMainBoards();

        //then
        assertThat(mainBoards.stream().anyMatch(b -> b.getId().equals(board1.getId()))).isTrue();
        assertThat(mainBoards.stream().noneMatch(b -> b.getId().equals(board2.getId()))).isTrue();
    }

    @Test
    void 메인게시판_순서_변경() {
        //given
        Board board1 = boardService.create(uniqueName(), "설명1", testSectionId);
        Board board2 = boardService.create(uniqueName(), "설명2", testSectionId);
        Board board3 = boardService.create(uniqueName(), "설명3", testSectionId);
        boardService.toggleMain(board1.getId()); // order 1
        boardService.toggleMain(board2.getId()); // order 2
        boardService.toggleMain(board3.getId()); // order 3

        //when - 순서 변경: board3 -> board1 -> board2
        List<BoardOrder> newOrders = List.of(
                new BoardOrder(board3.getId(), 1),
                new BoardOrder(board1.getId(), 2),
                new BoardOrder(board2.getId(), 3)
        );
        boardService.reorderMainBoards(newOrders);

        //then
        var mainBoards = boardService.findMainBoards();
        assertThat(mainBoards.get(0).getId()).isEqualTo(board3.getId());
        assertThat(mainBoards.get(1).getId()).isEqualTo(board1.getId());
        assertThat(mainBoards.get(2).getId()).isEqualTo(board2.getId());
    }
}
