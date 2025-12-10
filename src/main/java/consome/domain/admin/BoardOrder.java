package consome.domain.admin;

public record BoardOrder(
        Long sectionId,
        Long boardId,
        int displayOrder
) {
}
