package consome.application.admin;

import consome.domain.board.Board;
import consome.domain.board.BoardService;
import consome.domain.board.Category;
import consome.domain.board.CategoryService;
import consome.domain.post.PostService;
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

    public void delete(Long boardId) {
        boardService.delete(boardId);
    }

    public List<Category> getCategories(Long boardId) {
        return categoryService.getCategories(boardId);
    }

    public Page<BoardPostsResult> getPosts(Long refBoardId, Pageable pageable) {
        return postService.getPostByBoard(refBoardId, pageable)
                .map(post -> new BoardPostsResult(
                        post.postId(),
                        post.title(),
                        post.authorId(),
                        post.createdAt(),
                        post.viewCount(),
                        post.likeCount(),
                        post.dislikeCount(),
                        post.commentCount()
                ));
    }
}
