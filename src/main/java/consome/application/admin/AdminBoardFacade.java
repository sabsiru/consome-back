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

    public Board create(String name, String description, int displayOrder) {
        Board board = boardService.create(name, description, displayOrder);
        categoryService.create(board.getId(), "자유", 1);
        return board;
    }

    public Board update(Long boardId, String name, String description) {
        return boardService.update(boardId, name, description);
    }

    public Board changeOrder(Long boardId, int newOrder) {
        return boardService.changeOrder(boardId, newOrder);
    }

    public void reorder(List<BoardOrder> orders) {
        boardService.reorder(orders);
    }

    public void delete(Long boardId) {
        boardService.delete(boardId);
    }

    public List<Category> findAllOrderedByBoard(Long boardId) {
        return categoryService.findAllOrderedByBoard(boardId);
    }
}
