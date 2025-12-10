package consome.interfaces.admin.dto;

import java.util.List;

public record BoardReorderRequest(
        List<OrderItem> orders
) {
    public record OrderItem(Long sectionId, Long boardId, int displayOrder) {}
}
