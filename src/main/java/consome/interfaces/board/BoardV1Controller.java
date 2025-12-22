package consome.interfaces.board;

import consome.application.admin.BoardFacade;
import consome.application.post.PostPagingResult;
import consome.interfaces.board.dto.BoardPostListResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
