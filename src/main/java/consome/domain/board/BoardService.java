package consome.domain.board;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BoardService {
    private final BoardRepository boardRepository;

    public Board create(Long categoryId, String name, String description, int displayOrder) {
        Board board = Board.create(categoryId, name, description, displayOrder);
        return boardRepository.save(board);
    }

    public Board rename(Long boardId, String newName) {
        Board board = findById(boardId);
        board.rename(newName);
        return boardRepository.save(board);
    }

    public Board changeOrder(Long boardId, int newOrder) {
        Board board = findById(boardId);
        board.changeOrder(newOrder);
        return boardRepository.save(board);
    }

    public void delete(Long boardId) {
        Board board = findById(boardId);
        board.delete();
        boardRepository.save(board);
    }

    public Board findById(Long boardId) {
        return boardRepository.findById(boardId)
                .orElseThrow(() -> new IllegalArgumentException("잘못된 접근입니다."));
    }

}
