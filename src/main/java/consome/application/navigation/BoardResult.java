package consome.application.navigation;

public record BoardResult(
        Long boardId,
        String boardName,
        int displayOrder,
        boolean writeEnabled,
        boolean commentEnabled
) {
    // 기존 호환용 생성자
    public BoardResult(Long boardId, String boardName, int displayOrder) {
        this(boardId, boardName, displayOrder, true, true);
    }
}
