package consome.interfaces.board.v1;

import consome.application.board.BoardFacade;
import consome.application.post.PostPagingResult;
import consome.infrastructure.security.CustomUserDetails;
import consome.interfaces.admin.dto.CategoryResponse;
import consome.interfaces.board.dto.BoardPostListResponse;
import consome.interfaces.board.dto.BoardSearchResponse;
import consome.interfaces.board.dto.FavoriteBoardResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
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
            @PageableDefault(size = 50) Pageable pageable,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(defaultValue = "false") boolean headerOnly,
            @RequestParam(defaultValue = "false") boolean popular,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        Long userId = (!headerOnly && userDetails != null) ? userDetails.getUserId() : null;
        PostPagingResult result = boardFacade.getPosts(boardId, pageable, categoryId, userId, popular);
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

    @PostMapping("/{boardId}/favorites")
    public ResponseEntity<Void> addFavorite(
            @PathVariable Long boardId,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        boardFacade.addFavorite(boardId, userDetails.getUserId());
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @DeleteMapping("/{boardId}/favorites")
    public ResponseEntity<Void> removeFavorite(
            @PathVariable Long boardId,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        boardFacade.removeFavorite(boardId, userDetails.getUserId());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/favorites")
    public ResponseEntity<List<FavoriteBoardResponse>> getFavoriteBoards(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        List<FavoriteBoardResponse> result = boardFacade.getFavoriteBoards(userDetails.getUserId())
                .stream()
                .map(FavoriteBoardResponse::from)
                .toList();
        return ResponseEntity.ok(result);
    }
}
