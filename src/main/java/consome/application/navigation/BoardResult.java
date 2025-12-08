package consome.application.navigation;

public record BoardResult(
        Long boardId,
        String boardName,
        int displayOrder
) {
}
