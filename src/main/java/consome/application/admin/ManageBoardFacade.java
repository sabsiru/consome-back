package consome.application.admin;

import consome.domain.admin.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ManageBoardFacade {

    private final BoardService boardService;
    private final CategoryService categoryService;

    public Board create(String name, String description, int displayOrder) {
        return boardService.create(name, description, displayOrder);
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
