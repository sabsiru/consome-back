package consome.application.post;

public record PostCommand(
        Long boardId,
        Long categoryId,
        Long userId,
        String title,
        String content
) {
    public static PostCommand of(Long boardId, Long categoryId, Long userId, String title, String content) {
        return new PostCommand(boardId, categoryId,userId, title, content);
    }

}
