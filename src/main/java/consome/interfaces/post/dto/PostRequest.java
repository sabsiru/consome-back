package consome.interfaces.post.dto;

public record PostRequest(
        Long boardId,
        Long categoryId,
        Long userId,
        String title,
        String content
) {
}
