package consome.application.admin;

import consome.domain.admin.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminBoardFacade {

    private final BoardService boardService;
    private final CategoryService categoryService;

    public Board create(String name, String description, Long sectionId) {
        Board board = boardService.create(name, description, sectionId);
        categoryService.create(board.getId(), "공지사항", 1);
        categoryService.create(board.getId(), "자유", 2);
        return board;
    }

    public Board update(Long boardId, String name, String description) {
        return boardService.update(boardId, name, description);
    }

    public void reorderMainBoards(List<BoardOrder> orders) {
        boardService.reorderMainBoards(orders);
    }

    public void delete(Long boardId) {
        boardService.delete(boardId);
    }

    public List<Category> findAllOrderedByBoard(Long boardId) {
        return categoryService.findAllOrderedByBoard(boardId);
    }

    public Board toggleMain(Long boardId) {
        return boardService.toggleMain(boardId);
    }

    public Board toggleWriteEnabled(Long boardId) {
        return boardService.toggleWriteEnabled(boardId);
    }

    public Board toggleCommentEnabled(Long boardId) {
        return boardService.toggleCommentEnabled(boardId);
    }
}
