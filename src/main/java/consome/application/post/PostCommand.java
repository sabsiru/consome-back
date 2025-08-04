package consome.application.post;

public record PostCommand (
        Long refUserId,
        Long boardId,
        Long categoryId,
        String title,
        String content
){
}
