package consome.application.post;

public record PostCommand(
        Long refUserId,
        Long refBoardId,
        Long refCategoryId,
        String title,
        String content
) {
    public static PostCommand of(Long refBoardId, Long refCategoryId, Long refUserId, String title, String content) {
        return new PostCommand(refBoardId, refCategoryId, refUserId, title, content);
    }

}
