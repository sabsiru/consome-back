package consome.application.admin;

import consome.application.admin.result.ManageTreeResult;
import consome.application.user.UserSearchCommand;
import consome.application.user.UserSearchPagingResult;
import consome.application.user.UserSearchResult;
import consome.domain.admin.Board;
import consome.domain.admin.BoardManager;
import consome.domain.admin.BoardManagerService;
import consome.domain.admin.BoardQueryRepository;
import consome.domain.admin.BoardService;
import consome.domain.admin.Category;
import consome.domain.admin.CategoryService;
import consome.domain.user.UserInfo;
import consome.domain.user.UserQueryRepository;
import consome.domain.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ManageFacade {
    private final BoardService boardService;
    private final BoardQueryRepository boardQueryRepository;
    private final CategoryService categoryService;
    private final UserService userService;
    private final UserQueryRepository userQueryRepository;
    private final BoardManagerService boardManagerService;

    public ManageTreeResult getTree() {
        List<Board> boards = boardService.findAllOrdered();
        List<Category> categories = categoryService.findAllOrdered();

        Map<Long, List<Category>> catsByBoard = categories.stream()
                .collect(Collectors.groupingBy(Category::getBoardId));

        List<ManageTreeResult.BoardNode> boardNodes = boards.stream()
                .map(board -> new ManageTreeResult.BoardNode(
                        board.getId(),
                        board.getName(),
                        board.getDescription(),
                        board.getDisplayOrder(),
                        catsByBoard.getOrDefault(board.getId(), List.of()).stream()
                                .map(cat -> new ManageTreeResult.CategoryNode(
                                        cat.getId(),
                                        cat.getName(),
                                        cat.getDisplayOrder()
                                ))
                                .toList()
                ))
                .toList();

        return new ManageTreeResult(boardNodes);
    }

    public UserPagingResult getUsers(Pageable pageable) {
        Page<UserInfo> page = userService.findUsers(pageable);

        List<UserRowResult> content = page.getContent().stream()
                .map(userInfo -> new UserRowResult(
                        userInfo.userId(),
                        userInfo.loginId(),
                        userInfo.nickname(),
                        userInfo.role(),
                        userInfo.userPoint(),
                        boardManagerService.getManagedBoards(userInfo.userId())
                ))
                .toList();

        return new UserPagingResult(
                content,
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages()
        );
    }

    @Transactional(readOnly = true)
    public UserSearchPagingResult searchUsers(UserSearchCommand command, Pageable pageable) {
        Page<UserSearchResult> page = userQueryRepository.search(command, pageable);

        return new UserSearchPagingResult(
                page.getContent(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages()
        );
    }

    @Transactional(readOnly = true)
    public BoardPagingResult getBoards(Pageable pageable) {
        Page<BoardSearchResult> page = boardQueryRepository.findBoards(pageable);

        return new BoardPagingResult(
                page.getContent(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages()
        );
    }

    @Transactional(readOnly = true)
    public BoardPagingResult searchBoards(BoardSearchCommand command, Pageable pageable) {
        Page<BoardSearchResult> page = boardQueryRepository.search(command, pageable);

        return new BoardPagingResult(
                page.getContent(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages()
        );
    }

    @Transactional
    public BoardManager assignManager(Long boardId, Long userId) {
        return boardManagerService.assignManager(boardId, userId);
    }

    @Transactional
    public void removeManager(Long boardId, Long userId) {
        boardManagerService.removeManager(boardId, userId);
    }

    @Transactional(readOnly = true)
    public List<ManagerResult> getManagersByBoard(Long boardId) {
        Board board = boardService.findById(boardId);
        return boardManagerService.getManagersByBoard(boardId).stream()
                .map(bm -> new ManagerResult(
                        bm.getUserId(),
                        userService.getNicknameById(bm.getUserId()),
                        board.getName()
                ))
                .toList();
    }
}
