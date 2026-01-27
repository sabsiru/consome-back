package consome.domain.admin;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BoardService {
    private final BoardRepository boardRepository;

    public Board create(String name, String description, int displayOrder) {
        isNameDuplicate(name);
        Board board = Board.create(name, description, displayOrder);
        return boardRepository.save(board);
    }

    public Board update(Long boardId, String name, String description) {
        Board board = findById(boardId);
        if (name != null) {
            isNameDuplicate(name);
            board.rename(name);
        }
        if (description != null) {
            board.changeDescription(description);
        }
        return boardRepository.save(board);
    }

    public Board changeOrder(Long boardId, int newOrder) {
        Board board = findById(boardId);
        board.changeOrder(newOrder);
        return boardRepository.save(board);
    }

    @Transactional
    public void reorder(List<BoardOrder> orders) {
        List<Board> boards = boardRepository.findByDeletedFalseOrderByDisplayOrder();

        // 1️⃣ 임시 음수화
        for (Board board : boards) {
            board.changeOrder(-board.getDisplayOrder());
        }
        boardRepository.flush();

        // 2️⃣ 실제 순서 반영
        for (BoardOrder order : orders) {
            Board board = boardRepository.findById(order.boardId())
                    .orElseThrow(() -> new IllegalArgumentException("잘못된 접근입니다."));
            board.changeOrder(order.displayOrder());
        }
        boardRepository.flush();
    }

    public void delete(Long boardId) {
        Board board = findById(boardId);
        board.delete();
        boardRepository.save(board);
    }

    public boolean isNameDuplicate(String name) {
        if (boardRepository.existsByName(name)) {
            throw new IllegalArgumentException("이미 존재하는 게시판 이름입니다.");
        }
        if (name == null || name.trim().isEmpty() || name.length() < 1 || name.length() > 10) {
            throw new IllegalArgumentException("게시판 이름은 1자 이상 10자 이하로 입력해야 합니다.");
        }
        return boardRepository.existsByName(name);
    }

    public Board findById(Long boardId) {
        return boardRepository.findById(boardId)
                .orElseThrow(() -> new IllegalArgumentException("잘못된 접근입니다."));
    }

    public List<Board> findAllOrdered() {
        return boardRepository.findByDeletedFalseOrderByDisplayOrder();
    }

    public String findNameById(Long boardId) {
        Board board = findById(boardId);
        return board.getName();
    }
}
