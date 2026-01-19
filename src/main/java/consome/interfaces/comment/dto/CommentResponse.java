package consome.interfaces.comment.dto;

import consome.application.comment.CommentResult;

import java.time.LocalDateTime;

public record CommentResponse(
        Long commentId,
        Long postId,
        Long userId,
        String userNickname,
        String content,
        int depth,
        Boolean isDeleted,
        LocalDateTime createdAt,
        LocalDateTime updatedAt) {

    public static CommentResponse from(CommentResult comment) {
        return new CommentResponse(
                comment.commentId(),
                comment.postId(),
                comment.userId(),
                comment.userNickname(),
                comment.content(),
                comment.depth(),
                comment.isDeleted(),
                comment.createdAt(),
                comment.updatedAt()
        );
    }
}
