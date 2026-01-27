package consome.application.navigation;

import consome.domain.admin.Board;
import consome.domain.admin.BoardService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NavigationFacade {
    private final BoardService boardService;

    public List<BoardResult> getHeaderBoards() {
        List<Board> boards = boardService.findAllOrdered();

        return boards.stream()
                .map(board -> new BoardResult(
                        board.getId(),
                        board.getName(),
                        board.getDisplayOrder()
                ))
                .toList();
    }

    public List<Board> getBoards() {
        return boardService.findAllOrdered();
    }
}
