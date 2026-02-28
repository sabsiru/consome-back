package consome.application.admin;

import consome.domain.admin.*;
import consome.domain.admin.repository.BoardManagerRepository;
import consome.domain.common.exception.BusinessException;
import consome.domain.user.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminBoardFacade {

    private final BoardService boardService;
    private final CategoryService categoryService;
    private final BoardManagerRepository boardManagerRepository;

    private void validateManagerAccess(Long boardId, Long userId, Role userRole) {
        if (userRole == Role.ADMIN) return;

        if (!boardManagerRepository.existsByBoardIdAndUserId(boardId, userId)) {
            throw new BusinessException("FORBIDDEN", "해당 게시판을 관리할 권한이 없습니다.");
        }
    }

    private void validateAdminOnly(Role userRole) {
        if (userRole != Role.ADMIN) {
            throw new BusinessException("FORBIDDEN", "관리자만 접근할 수 있습니다.");
        }
    }

    public Board create(String name, String description, Long sectionId, Role userRole) {
        validateAdminOnly(userRole);
        Board board = boardService.create(name, description, sectionId);
        categoryService.create(board.getId(), "공지사항", 1);
        categoryService.create(board.getId(), "자유", 2);
        return board;
    }

    public Board update(Long boardId, String name, String description, Long userId, Role userRole) {
        validateManagerAccess(boardId, userId, userRole);
        return boardService.update(boardId, name, description);
    }

    public void reorderMainBoards(List<BoardOrder> orders, Role userRole) {
        validateAdminOnly(userRole);
        boardService.reorderMainBoards(orders);
    }

    public void delete(Long boardId, Role userRole) {
        validateAdminOnly(userRole);
        boardService.delete(boardId);
    }

    public List<Category> findAllOrderedByBoard(Long boardId) {
        return categoryService.findAllOrderedByBoard(boardId);
    }

    public Board toggleMain(Long boardId, Role userRole) {
        validateAdminOnly(userRole);
        return boardService.toggleMain(boardId);
    }

    public Board toggleWriteEnabled(Long boardId, Long userId, Role userRole) {
        validateManagerAccess(boardId, userId, userRole);
        return boardService.toggleWriteEnabled(boardId);
    }

    public Board toggleCommentEnabled(Long boardId, Long userId, Role userRole) {
        validateManagerAccess(boardId, userId, userRole);
        return boardService.toggleCommentEnabled(boardId);
    }
}
