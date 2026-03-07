package consome.application.board;

import consome.application.post.PostPagingResult;
import consome.application.post.PostRowResult;
import consome.domain.admin.Board;
import consome.domain.admin.BoardManager;
import consome.domain.admin.repository.BoardManagerRepository;
import consome.domain.admin.BoardService;
import consome.domain.admin.Category;
import consome.domain.admin.CategoryService;
import consome.domain.admin.repository.BoardRepository;
import consome.domain.board.entity.BoardFavorite;
import consome.domain.board.repository.BoardFavoriteRepository;
import consome.domain.common.exception.BusinessException;
import consome.domain.post.PostService;
import consome.domain.post.PostSummary;
import consome.domain.user.User;
import consome.domain.user.UserService;
import consome.infrastructure.redis.VisitedBoardRedisRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BoardFacade {

    private final BoardService boardService;
    private final PostService postService;
    private final CategoryService categoryService;
    private final BoardManagerRepository boardManagerRepository;
    private final UserService userService;
    private final VisitedBoardRedisRepository visitedBoardRedisRepository;
    private final BoardFavoriteRepository boardFavoriteRepository;
    private final BoardRepository boardRepository;

    public PostPagingResult getPosts(Long boardId, Pageable pageable, Long categoryId, Long userId) {
        if (userId != null) {
            visitedBoardRedisRepository.recordVisit(userId, boardId);
        }

        Board board = boardService.findById(boardId);
        boolean isFavorited = userId != null && boardFavoriteRepository.existsByUserIdAndBoardId(userId, boardId);

        Page<PostSummary> page = postService.findBoardPosts(boardId, pageable, categoryId);

        List<PostRowResult> rows = page.getContent().stream()
                .map(summary -> new PostRowResult(
                        summary.postId(),
                        summary.title(),
                        summary.categoryId(),
                        summary.categoryName(),
                        summary.authorId(),
                        summary.authorNickname(),
                        summary.authorLevel(),
                        summary.authorRole(),
                        summary.viewCount(),
                        summary.likeCount(),
                        summary.dislikeCount(),
                        summary.commentCount(),
                        summary.createdAt(),
                        summary.updatedAt(),
                        summary.deleted(),
                        summary.isPinned(),
                        summary.pinnedOrder(),
                        summary.hasMedia()
                ))
                .toList();

        List<PostPagingResult.ManagerInfo> managers = boardManagerRepository.findByBoardId(boardId).stream()
                .map(bm -> {
                    User user = userService.findById(bm.getUserId());
                    return new PostPagingResult.ManagerInfo(user.getId(), user.getNickname());
                })
                .toList();

        return new PostPagingResult(
                board.getId(),
                board.getName(),
                board.getDescription(),
                board.isWriteEnabled(),
                board.isCommentEnabled(),
                isFavorited,
                rows,
                managers,
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages()
        );
    }

    public List<Category> getCategories(Long boardId) {
        return categoryService.findAllOrderedByBoard(boardId);
    }

    public String findNameById(Long boardId) {
        Board board = boardService.findById(boardId);
        return board.getName();
    }

    public List<UserBoardSearchResult> searchBoards(String keyword, int limit) {
        return boardService.searchByKeyword(keyword, limit);
    }

    public PostPagingResult searchPosts(Long boardId, String keyword, String searchType, Pageable pageable) {
        Board board = boardService.findById(boardId);

        Page<PostSummary> page = postService.searchPosts(boardId, keyword, searchType, pageable);

        List<PostRowResult> rows = page.getContent().stream()
                .map(summary -> new PostRowResult(
                        summary.postId(),
                        summary.title(),
                        summary.categoryId(),
                        summary.categoryName(),
                        summary.authorId(),
                        summary.authorNickname(),
                        summary.authorLevel(),
                        summary.authorRole(),
                        summary.viewCount(),
                        summary.likeCount(),
                        summary.dislikeCount(),
                        summary.commentCount(),
                        summary.createdAt(),
                        summary.updatedAt(),
                        summary.deleted(),
                        summary.isPinned(),
                        summary.pinnedOrder(),
                        summary.hasMedia()
                ))
                .toList();

        List<PostPagingResult.ManagerInfo> managers = boardManagerRepository.findByBoardId(boardId).stream()
                .map(bm -> {
                    User user = userService.findById(bm.getUserId());
                    return new PostPagingResult.ManagerInfo(user.getId(), user.getNickname());
                })
                .toList();

        return new PostPagingResult(
                board.getId(),
                board.getName(),
                board.getDescription(),
                board.isWriteEnabled(),
                board.isCommentEnabled(),
                false,
                rows,
                managers,
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages()
        );
    }

    @Transactional
    public void addFavorite(Long boardId, Long userId) {
        Board board = boardService.findById(boardId);
        if (board.isDeleted()) {
            throw new BusinessException("BOARD_NOT_FOUND", "존재하지 않는 게시판입니다.");
        }
        if (boardFavoriteRepository.existsByUserIdAndBoardId(userId, boardId)) {
            throw new BusinessException("FAVORITE_ALREADY_EXISTS", "이미 즐겨찾기한 게시판입니다.");
        }
        boardFavoriteRepository.save(BoardFavorite.of(userId, boardId));
    }

    @Transactional
    public void removeFavorite(Long boardId, Long userId) {
        BoardFavorite favorite = boardFavoriteRepository.findByUserIdAndBoardId(userId, boardId)
                .orElseThrow(() -> new BusinessException("FAVORITE_NOT_FOUND", "즐겨찾기하지 않은 게시판입니다."));
        boardFavoriteRepository.delete(favorite);
    }

    @Transactional(readOnly = true)
    public List<FavoriteBoardResult> getFavoriteBoards(Long userId) {
        List<Long> boardIds = boardFavoriteRepository.findByUserId(userId).stream()
                .map(BoardFavorite::getBoardId)
                .toList();

        if (boardIds.isEmpty()) return List.of();

        Map<Long, Board> boardMap = boardRepository.findAllById(boardIds).stream()
                .collect(Collectors.toMap(Board::getId, Function.identity()));

        return boardIds.stream()
                .map(boardMap::get)
                .filter(board -> board != null && !board.isDeleted())
                .map(board -> new FavoriteBoardResult(board.getId(), board.getName(), board.getDescription()))
                .toList();
    }
}
