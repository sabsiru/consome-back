package consome.interfaces.comment.v1;

import consome.application.comment.CommentFacade;
import consome.domain.comment.Comment;
import consome.interfaces.comment.dto.CommentResponse;
import consome.interfaces.comment.dto.CreateCommentRequest;
import consome.interfaces.comment.mapper.CommentResponseMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/posts/")
public class CommentV1Controller {

    private final CommentFacade commentFacade;

    /**
     * to-do: 댓글 페이징 처리
     */
//    // 목록
//    @GetMapping("/posts/{postId}/comments")
//    public ResponseEntity<CommentPageResponse> list(
//            @PathVariable Long postId,
//            @RequestParam(required = false) Long cursorId,
//            @RequestParam(defaultValue = "20") int size,
//            @RequestParam(defaultValue = "createdAt") String sort) {
//
//        commentFacade.listByPost(postId, cursorId, size, sort);
//        return ResponseEntity.ok(CommentResponseMapper.toPageResponse(page));
//    }
    @PostMapping("/{postId}/comments")
    public ResponseEntity<CommentResponse> create(
            @PathVariable Long postId,
            @RequestBody @Valid CreateCommentRequest request) {

        Comment comment = commentFacade.comment(postId, request.userId(), request.parentId(), request.content());
        return ResponseEntity.ok(CommentResponseMapper.toResponse(comment));
    }
}
