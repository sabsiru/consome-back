package consome.interfaces.comment.dto;

import consome.domain.comment.Comment;

import java.time.LocalDateTime;

public record CommentResponse(
        Long commentId,
        Long postId,
        Long userId,
        String content,
        LocalDateTime createdAt) {

    public static CommentResponse from(Comment comment) {
        return new CommentResponse(
                comment.getId(),
                comment.getPostId(),
                comment.getUserId(),
                comment.getContent(),
                comment.getCreatedAt()
        );
    }
}
