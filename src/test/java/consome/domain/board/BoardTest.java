package consome.domain.board;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class BoardTest {

    @Test
    void 게시판_생성_성공_tester() {
        // given
        Long sectionId = 1L;
        String name = "LOL 자유 게시판";
        String description = "자유롭게 이야기하세요";
        int order = 1;

        // when
        Board board = Board.create(sectionId, name, description, order);

        // then
        assertThat(board.getRefSectionId()).isEqualTo(sectionId);
        assertThat(board.getName()).isEqualTo(name);
        assertThat(board.getDescription()).isEqualTo(description);
        assertThat(board.getDisplayOrder()).isEqualTo(order);
        assertThat(board.isDeleted()).isFalse();
        assertThat(board.getCreatedAt()).isNotNull();
        assertThat(board.getUpdatedAt()).isNotNull();
    }

    @Test
    void 게시판_이름_변경_성공_tester() {
        Board board = Board.create(1L, "옛 이름", "설명", 1);

        board.rename("새 이름");

        assertThat(board.getName()).isEqualTo("새 이름");
    }

    @Test
    void 게시판_설명_변경_성공_tester() {
        Board board = Board.create(1L, "이름", "이전 설명", 1);

        board.changeDescription("새 설명");

        assertThat(board.getDescription()).isEqualTo("새 설명");
    }

    @Test
    void 게시판_정렬순서_변경_성공_tester() {
        Board board = Board.create(1L, "이름", "설명", 1);

        board.changeOrder(5);

        assertThat(board.getDisplayOrder()).isEqualTo(5);
    }

    @Test
    void 게시판_삭제_성공_tester() {
        Board board = Board.create(1L, "이름", "설명", 1);

        board.delete();

        assertThat(board.isDeleted()).isTrue();
    }
}