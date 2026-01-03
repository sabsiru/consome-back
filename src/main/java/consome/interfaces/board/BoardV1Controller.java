package consome.interfaces.board;

import consome.application.board.BoardFacade;
import consome.application.post.PostPagingResult;
import consome.interfaces.admin.dto.CategoryResponse;
import consome.interfaces.board.dto.BoardPostListResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/boards")
public class BoardV1Controller {

    private final BoardFacade boardFacade;

    @GetMapping("/{boardId}/posts")
    public ResponseEntity<BoardPostListResponse> getPosts(
            @PathVariable Long boardId,
            @PageableDefault(size = 20) Pageable pageable
    ) {
        PostPagingResult result = boardFacade.getPosts(boardId, pageable);
        return ResponseEntity.ok(BoardPostListResponse.from(result));
    }

    @GetMapping("/{boardId}/categories")
    public ResponseEntity<List<CategoryResponse>> getCategories(@PathVariable Long boardId) {
        List<CategoryResponse> categories = boardFacade.getCategories(boardId).stream()
                .map(CategoryResponse::from)
                .toList();
        return ResponseEntity.ok(categories);
    }

    @GetMapping("/{boardId}")
    public ResponseEntity<String> findNameById(@PathVariable Long boardId) {
        return ResponseEntity.ok(boardFacade.findNameById(boardId));
    }
}
