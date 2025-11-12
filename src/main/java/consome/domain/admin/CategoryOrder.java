package consome.domain.admin;

public record CategoryOrder(
        Long boardId,
        Long categoryId,
        int displayOrder
) {
}
