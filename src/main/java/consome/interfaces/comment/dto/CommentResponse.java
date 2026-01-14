package consome.interfaces.comment.dto;

import consome.application.comment.CommentResult;
import consome.domain.comment.Comment;

import java.time.LocalDateTime;

public record CommentResponse(
        Long commentId,
        Long postId,
        Long userId,
        String userNickname,
        String content,
        int depth,
        LocalDateTime createdAt) {

    public static CommentResponse from(CommentResult comment) {
        return new CommentResponse(
                comment.commentId(),
                comment.postId(),
                comment.userId(),
                comment.userNickname(),
                comment.content(),
                comment.depth(),
                comment.createdAt()
        );
    }
}
