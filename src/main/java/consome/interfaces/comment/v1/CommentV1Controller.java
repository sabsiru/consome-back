package consome.interfaces.comment.v1;

import consome.application.comment.CommentFacade;
import consome.application.comment.CommentListResult;
import consome.application.comment.CommentResult;
import consome.domain.comment.Comment;
import consome.domain.comment.CommentReaction;
import consome.domain.post.ReactionType;
import consome.interfaces.comment.dto.*;
import consome.interfaces.comment.mapper.CommentResponseMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/posts/")
public class CommentV1Controller {

    private final CommentFacade commentFacade;

    @GetMapping("{postId}/comments")
    public ResponseEntity<CommentPageResponse> list(
            @PathVariable Long postId,
            @PageableDefault(size = 50) Pageable pageable) {

        Page<CommentListResult> page = commentFacade.listByPost(postId, pageable);
        return ResponseEntity.ok(CommentResponseMapper.toPageResponse(page));
    }

    @PostMapping("/{postId}/comments")
    public ResponseEntity<CommentResponse> comment(
            @PathVariable Long postId,
            @RequestBody @Valid CreateCommentRequest request) {

        CommentResult result = commentFacade.comment(postId, request.userId(), request.parentId(), request.content());
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

        commentFacade.delete(userId, commentId);
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
    public ResponseEntity<CommentReaction> toggleReaction(
            @PathVariable Long postId,
            @PathVariable Long commentId,
            @RequestParam Long userId,
            @RequestParam ReactionType type) {

        CommentReaction commentReaction = commentFacade.toggleReaction(commentId, userId, type);
        return ResponseEntity.ok(commentReaction);
    }
}
