package consome.interfaces.comment.v1;

import consome.application.comment.CommentFacade;
import consome.application.comment.CommentPageResult;
import consome.application.comment.CommentResult;
import consome.domain.comment.Comment;
import consome.domain.comment.CommentStat;
import consome.infrastructure.aop.RateLimit;
import consome.infrastructure.aop.RequireEmailVerified;
import consome.infrastructure.security.CustomUserDetails;
import consome.interfaces.comment.dto.*;
import consome.interfaces.comment.mapper.CommentResponseMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/posts/")
public class CommentV1Controller {

    private final CommentFacade commentFacade;

    @GetMapping("{postId}/comments")
    public ResponseEntity<CommentPageResponse> list(
            @PathVariable Long postId,
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PageableDefault(size = 50) Pageable pageable) {

        Long userId = userDetails != null ? userDetails.getUserId() : null;
        CommentPageResult result = commentFacade.listByPost(postId, userId, pageable);
        return ResponseEntity.ok(CommentResponseMapper.toPageResponse(result));
    }

    @PostMapping("/{postId}/comments")
    @RequireEmailVerified
    @RateLimit(key = "comment", limit = 20)
    public ResponseEntity<CommentResponse> comment(
            @PathVariable Long postId,
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody @Valid CreateCommentRequest request) {

        CommentResult result = commentFacade.comment(postId, userDetails.getUserId(), request.parentId(), request.content());
        CommentResponse response = new CommentResponse(
                result.commentId(),
                result.postId(),
                result.userId(),
                result.userNickname(),
                result.content(),
                result.depth(),
                result.isDeleted(),
                result.createdAt(),
                result.updatedAt()
        );
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{postId}/comments/{commentId}")
    public ResponseEntity<EditCommentResponse> edit(
            @PathVariable Long postId,
            @PathVariable Long commentId,
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody @Valid EditCommentRequest request) {

        Comment comment = commentFacade.edit(userDetails.getUserId(), commentId, request.content());
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
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        commentFacade.delete(userDetails.getUserId(), commentId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{postId}/comments/{commentId}/like")
    public ResponseEntity<CommentStatResponse> like(
            @PathVariable Long postId,
            @PathVariable Long commentId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        CommentStat stat = commentFacade.like(commentId, userDetails.getUserId());
        return ResponseEntity.ok(CommentStatResponse.from(stat));
    }

    @PostMapping("/{postId}/comments/{commentId}/dislike")
    public ResponseEntity<CommentStatResponse> dislike(
            @PathVariable Long postId,
            @PathVariable Long commentId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        CommentStat stat = commentFacade.dislike(commentId, userDetails.getUserId());
        return ResponseEntity.ok(CommentStatResponse.from(stat));
    }
}
