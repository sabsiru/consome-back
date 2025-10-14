package consome.interfaces.comment.dto;

public record EditCommentResponse(
        Long commentId,
        Long postId,
        Long userId,
        String content
) {
}
