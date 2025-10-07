package consome.interfaces.comment.v1;

import consome.application.comment.CommentFacade;
import consome.domain.comment.Comment;
import consome.domain.post.ReactionType;
import consome.interfaces.comment.dto.CommentResponse;
import consome.interfaces.comment.dto.CreateCommentRequest;
import consome.interfaces.comment.dto.EditCommentRequest;
import consome.interfaces.comment.dto.EditCommentResponse;
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
        CommentResponse response = new CommentResponse(
                comment.getId(),
                comment.getPostId(),
                comment.getUserId(),
                comment.getContent(),
                comment.getCreatedAt()
        );
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{postId}/comments/{commentId}")
    public ResponseEntity<EditCommentResponse> edit(
            @PathVariable Long postId,
            @PathVariable Long commentId,
            @RequestBody @Valid EditCommentRequest request) {

        Comment comment = commentFacade.edit(request.userId(), commentId, request.content());
        EditCommentResponse response = new EditCommentResponse(
                comment.getId(),
                comment.getPostId(),
                comment.getUserId(),
                comment.getContent()
        );
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{postId}/comments/{commentId}")
    public ResponseEntity<Void> delete(
            @PathVariable Long postId,
            @PathVariable Long commentId,
            @RequestParam Long userId) {

        commentFacade.delete(commentId, userId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{postId}/comments/{commentId}/like")
    public ResponseEntity<Long> like(
            @PathVariable Long postId,
            @PathVariable Long commentId,
            @RequestParam Long userId) {

        long likeCount = commentFacade.like(commentId, userId);
        return ResponseEntity.ok(likeCount);
    }

    @PostMapping("/{postId}/comments/{commentId}/reaction")
    public ResponseEntity<Void> toggleReaction(
            @PathVariable Long postId,
            @PathVariable Long commentId,
            @RequestParam Long userId,
            @RequestParam ReactionType type) {

        commentFacade.toggleReaction(commentId, userId, type);
        return ResponseEntity.ok().build();
    }
}
