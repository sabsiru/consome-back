package consome.domain.admin;

import consome.application.board.UserBoardSearchResult;
import consome.domain.admin.repository.BoardQueryRepository;
import consome.domain.admin.repository.BoardRepository;
import consome.domain.admin.repository.SectionRepository;
import consome.domain.common.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BoardService {
    private final BoardRepository boardRepository;
    private final BoardQueryRepository boardQueryRepository;
    private final SectionRepository sectionRepository;

    public Board create(String name, String description, Long sectionId) {
        isNameDuplicate(name);
        if (!sectionRepository.existsById(sectionId)) {
            throw new BusinessException("SECTION_NOT_FOUND", "섹션을 찾을 수 없습니다.");
        }
        Board board = Board.create(name, description, sectionId);
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

    
    @Transactional
    public void reorderMainBoards(List<BoardOrder> orders) {
        // 1️⃣ 임시 음수화
        List<Board> mainBoards = boardRepository.findByIsMainTrueAndDeletedFalseOrderByDisplayOrder();
        for (Board board : mainBoards) {
            board.changeMainOrder(-board.getDisplayOrder() - 1);
        }
        boardRepository.flush();

        // 2️⃣ 실제 순서 반영
        for (BoardOrder order : orders) {
            Board board = boardRepository.findById(order.boardId())
                    .orElseThrow(() -> new BusinessException.BoardNotFound(order.boardId()));
            board.changeMainOrder(order.displayOrder());
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
            throw new BusinessException("BOARD_DUPLICATE_NAME", "이미 존재하는 게시판 이름입니다.");
        }
        if (name == null || name.trim().isEmpty() || name.length() < 1 || name.length() > 10) {
            throw new BusinessException("BOARD_INVALID_NAME", "게시판 이름은 1자 이상 10자 이하로 입력해야 합니다.");
        }
        return boardRepository.existsByName(name);
    }

    public Board findById(Long boardId) {
        return boardRepository.findById(boardId)
                .orElseThrow(() -> new BusinessException.BoardNotFound(boardId));
    }

    public List<Board> findAllOrdered() {
        return boardRepository.findByDeletedFalseOrderByDisplayOrder();
    }

    public String findNameById(Long boardId) {
        Board board = findById(boardId);
        return board.getName();
    }

    public List<UserBoardSearchResult> searchByKeyword(String keyword, int limit) {
        return boardQueryRepository.searchByKeyword(keyword, limit);
    }

    public Board toggleMain(Long boardId) {
        Board board = findById(boardId);
        if (board.isMain()) {
            // OFF: 순서 0으로 초기화
            board.setMain(false, 0);
        } else {
            // ON: 현재 메인 게시판 중 최대 순서 + 1
            int maxOrder = boardRepository.findByIsMainTrueAndDeletedFalseOrderByDisplayOrder()
                    .stream()
                    .mapToInt(Board::getDisplayOrder)
                    .max()
                    .orElse(0);
            board.setMain(true, maxOrder + 1);
        }
        return boardRepository.save(board);
    }

    public List<Board> findMainBoards() {
        return boardRepository.findByIsMainTrueAndDeletedFalseOrderByDisplayOrder();
    }

    public Board toggleWriteEnabled(Long boardId) {
        Board board = findById(boardId);
        board.toggleWriteEnabled();
        return boardRepository.save(board);
    }

    public Board toggleCommentEnabled(Long boardId) {
        Board board = findById(boardId);
        board.toggleCommentEnabled();
        return boardRepository.save(board);
    }
}
