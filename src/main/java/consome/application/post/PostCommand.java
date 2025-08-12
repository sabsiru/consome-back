package consome.application.post;

public record PostCommand (
        Long refUserId,
        Long refBoardId,
        Long refCategoryId,
        String title,
        String content
){
}
