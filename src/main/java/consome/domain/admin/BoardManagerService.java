package consome.domain.admin;

import consome.domain.admin.repository.BoardManagerRepository;
import consome.domain.admin.repository.BoardRepository;
import consome.domain.common.exception.BusinessException;
import consome.domain.user.Role;
import consome.domain.user.User;
import consome.domain.user.repository.UserRepository;
import consome.domain.user.exception.UserException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BoardManagerService {

    private final BoardManagerRepository boardManagerRepository;
    private final BoardRepository boardRepository;
    private final UserRepository userRepository;

    @Transactional
    public BoardManager assignManager(Long boardId, Long userId) {
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new BusinessException.BoardNotFound(boardId));

        if (boardManagerRepository.existsByBoardIdAndUserId(boardId, userId)) {
            throw new BusinessException("ALREADY_MANAGER", "이미 해당 게시판의 관리자입니다.");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserException.NotFound("사용자를 찾을 수 없습니다."));

        if (user.getRole() != Role.MANAGER) {
            user.updateRole(Role.MANAGER);
        }

        BoardManager manager = BoardManager.create(board.getId(), userId);
        return boardManagerRepository.save(manager);
    }

    @Transactional
    public void removeManager(Long boardId, Long userId) {
        BoardManager manager = boardManagerRepository.findByBoardIdAndUserId(boardId, userId)
                .orElseThrow(() -> new BusinessException("NOT_MANAGER", "해당 게시판의 관리자가 아닙니다."));

        boardManagerRepository.delete(manager);

        if (!boardManagerRepository.existsByUserId(userId)) {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new UserException.NotFound("사용자를 찾을 수 없습니다."));
            if (user.getRole() == Role.MANAGER) {
                user.updateRole(Role.USER);
            }
        }
    }

    @Transactional(readOnly = true)
    public List<BoardManager> getManagersByBoard(Long boardId) {
        return boardManagerRepository.findByBoardId(boardId);
    }

    @Transactional(readOnly = true)
    public List<String> getManagedBoardNames(Long userId) {
        List<BoardManager> managers = boardManagerRepository.findByUserId(userId);
        return managers.stream()
                .map(bm -> boardRepository.findById(bm.getBoardId())
                        .map(Board::getName)
                        .orElse(null))
                .filter(name -> name != null)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ManagedBoardInfo> getManagedBoards(Long userId) {
        List<BoardManager> managers = boardManagerRepository.findByUserId(userId);
        return managers.stream()
                .map(bm -> boardRepository.findById(bm.getBoardId())
                        .map(b -> new ManagedBoardInfo(b.getId(), b.getName()))
                        .orElse(null))
                .filter(info -> info != null)
                .toList();
    }
}
