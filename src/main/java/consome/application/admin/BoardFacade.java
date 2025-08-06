package consome.application.admin;

import consome.domain.board.Board;
import consome.domain.board.BoardService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BoardFacade {

    private final BoardService boardService;

    public Board create(Long sectionId, String name, String description, int displayOrder) {
        return boardService.create(sectionId, name, description, displayOrder);
    }

    public Board rename(Long boardId, String newName) {
        return boardService.rename(boardId, newName);
    }

    public Board changeOrder(Long boardId, int newOrder) {
        return boardService.changeOrder(boardId, newOrder);
    }

    public void delete(Long boardId) {
        boardService.delete(boardId);
    }
}
