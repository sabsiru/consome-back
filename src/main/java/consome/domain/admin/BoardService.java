package consome.domain.admin;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BoardService {
    private final BoardRepository boardRepository;

    public Board create(Long sectionId, String name, String description, int displayOrder) {
        isNameDuplicate(name);
        Board board = Board.create(sectionId, name, description, displayOrder);
        return boardRepository.save(board);
    }

    public Board rename(Long boardId, String newName) {
        Board board = findById(boardId);
        isNameDuplicate(newName);
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

    public List<Board> findAllBySectionId(Long sectionId) {
        return boardRepository.findByRefSectionIdAndDeletedFalseOrderByDisplayOrder(sectionId);
    }

    public List<Board> findAllOrdered() {
        return boardRepository.findByDeletedFalseOrderByDisplayOrder();
    }
}
