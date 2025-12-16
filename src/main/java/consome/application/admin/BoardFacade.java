package consome.application.admin;

import consome.application.post.PostPagingResult;
import consome.application.post.PostRowResult;
import consome.domain.admin.*;
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
    private final CategoryService categoryService;
    private final PostService postService;

    public Board create(Long sectionId, String name, String description, int displayOrder) {
        return boardService.create(sectionId, name, description, displayOrder);
    }

    public Board rename(Long boardId, String newName) {
        return boardService.rename(boardId, newName);
    }

    public Board changeOrder(Long boardId, int newOrder) {
        return boardService.changeOrder(boardId, newOrder);
    }

    public void reorder(List<BoardOrder> orders) {
        boardService.reorder(orders);
    }

    public void delete(Long boardId) {
        boardService.delete(boardId);
    }

    public List<Category> getCategories(Long boardId) {
        return categoryService.findAllOrderedByBoard(boardId);
    }

    public PostPagingResult getPosts(Long boardId, Pageable pageable) {
        Page<PostSummary> page = postService.findBoardPosts(boardId, pageable);

        List<PostRowResult> rows = page.getContent().stream()
                .map(summary -> new PostRowResult(
                        summary.postId(),
                        summary.title(),
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
        return new PostPagingResult(rows,
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages()
        );
    }
}
