package consome.interfaces.comment.dto;

public record CreateCommentRequest(
        Long userId,
        Long parentId,
        String content
) {
}
