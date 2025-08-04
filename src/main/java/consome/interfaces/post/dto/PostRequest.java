package consome.interfaces.post.dto;

public record PostRequest(
        Long refUserId,
        Long boardId,
        Long categoryId,
        String title,
        String content
) {
}
