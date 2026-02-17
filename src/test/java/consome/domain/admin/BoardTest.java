package consome.domain.admin;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class BoardTest {

    @Test
    void 게시판_생성_성공() {
        // given
        String name = "LOL 자유 게시판";
        String description = "자유롭게 이야기하세요";

        // when
        Board board = Board.create(name, description, 1L);

        // then
        assertThat(board.getName()).isEqualTo(name);
        assertThat(board.getDescription()).isEqualTo(description);
        assertThat(board.getDisplayOrder()).isEqualTo(0);
        assertThat(board.isDeleted()).isFalse();
        assertThat(board.isMain()).isFalse();
    }

    @Test
    void 게시판_이름_변경_성공() {
        Board board = Board.create("옛 이름", "설명", 1L);

        board.rename("새 이름");

        assertThat(board.getName()).isEqualTo("새 이름");
    }

    @Test
    void 게시판_설명_변경_성공() {
        Board board = Board.create("이름", "이전 설명", 1L);

        board.changeDescription("새 설명");

        assertThat(board.getDescription()).isEqualTo("새 설명");
    }

    @Test
    void 게시판_삭제_성공() {
        Board board = Board.create("이름", "설명", 1L);

        board.delete();

        assertThat(board.isDeleted()).isTrue();
    }

    @Test
    void 메인게시판_설정_성공() {
        Board board = Board.create("이름", "설명", 1L);
        assertThat(board.isMain()).isFalse();
        assertThat(board.getDisplayOrder()).isEqualTo(0);

        // 메인 ON
        board.setMain(true, 1);
        assertThat(board.isMain()).isTrue();
        assertThat(board.getDisplayOrder()).isEqualTo(1);

        // 메인 OFF
        board.setMain(false, 0);
        assertThat(board.isMain()).isFalse();
        assertThat(board.getDisplayOrder()).isEqualTo(0);
    }

    @Test
    void 메인게시판_순서_변경_성공() {
        Board board = Board.create("이름", "설명", 1L);
        board.setMain(true, 1);

        board.changeMainOrder(3);

        assertThat(board.getDisplayOrder()).isEqualTo(3);
    }
}
