package consome.interfaces.post.dto;

public record EditRequest(
        String title,
        Long categoryId,
        String content
) {
}
