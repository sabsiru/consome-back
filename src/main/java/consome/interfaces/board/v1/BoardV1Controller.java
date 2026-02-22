package consome.interfaces.board.v1;

import consome.application.board.BoardFacade;
import consome.application.post.PostPagingResult;
import consome.infrastructure.security.CustomUserDetails;
import consome.interfaces.admin.dto.CategoryResponse;
import consome.interfaces.board.dto.BoardPostListResponse;
import consome.interfaces.board.dto.BoardSearchResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/boards")
public class BoardV1Controller {

    private final BoardFacade boardFacade;

    @GetMapping("/{boardId}/posts")
    public ResponseEntity<BoardPostListResponse> getPosts(
            @PathVariable Long boardId,
            @PageableDefault(size = 20) Pageable pageable,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(defaultValue = "false") boolean headerOnly,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        // headerOnly=true면 방문 기록 안 함 (BoardLayout용)
        Long userId = (!headerOnly && userDetails != null) ? userDetails.getUserId() : null;
        PostPagingResult result = boardFacade.getPosts(boardId, pageable, categoryId, userId);
        return ResponseEntity.ok(BoardPostListResponse.from(result));
    }

    @GetMapping("/{boardId}/posts/search")
    public ResponseEntity<BoardPostListResponse> searchPosts(
            @PathVariable Long boardId,
            @RequestParam String keyword,
            @RequestParam(defaultValue = "all") String type,
            @PageableDefault(size = 20) Pageable pageable
    ) {
        PostPagingResult result = boardFacade.searchPosts(boardId, keyword, type, pageable);
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

    @GetMapping("/search")
    public ResponseEntity<List<BoardSearchResponse>> searchBoards(
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "10") int size
    ) {
        List<BoardSearchResponse> results = boardFacade.searchBoards(keyword, size)
                .stream()
                .map(BoardSearchResponse::from)
                .toList();
        return ResponseEntity.ok(results);
    }
}
