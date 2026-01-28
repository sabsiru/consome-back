package consome.application.board;

import consome.application.post.PostPagingResult;
import consome.application.post.PostRowResult;
import consome.domain.admin.Board;
import consome.domain.admin.BoardService;
import consome.domain.admin.Category;
import consome.domain.admin.CategoryService;
import consome.domain.post.PostService;
import consome.domain.post.PostSummary;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BoardFacade {

    private final BoardService boardService;
    private final PostService postService;
    private final CategoryService categoryService;

    public PostPagingResult getPosts(Long boardId, Pageable pageable, Long categoryId) {
        Board board = boardService.findById(boardId);

        Page<PostSummary> page = postService.findBoardPosts(boardId, pageable, categoryId);

        List<PostRowResult> rows = page.getContent().stream()
                .map(summary -> new PostRowResult(
                        summary.postId(),
                        summary.title(),
                        summary.categoryId(),
                        summary.categoryName(),
                        summary.authorId(),
                        summary.authorNickname(),
                        summary.viewCount(),
                        summary.likeCount(),
                        summary.dislikeCount(),
                        summary.commentCount(),
                        summary.createdAt(),
                        summary.updatedAt(),
                        summary.deleted()
                ))
                .toList();
        return new PostPagingResult(
                board.getId(),
                board.getName(),
                board.getDescription(),
                rows,
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
}
