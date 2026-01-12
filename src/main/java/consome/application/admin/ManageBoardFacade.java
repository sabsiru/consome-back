package consome.application.admin;

import consome.application.post.PostPagingResult;
import consome.application.post.PostRowResult;
import consome.domain.admin.*;
import consome.domain.post.PostService;
import consome.domain.post.PostSummary;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ManageBoardFacade {

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

    public void reorder(List<BoardOrder> orders) {
        boardService.reorder(orders);
    }

    public void delete(Long boardId) {
        boardService.delete(boardId);
    }
}
