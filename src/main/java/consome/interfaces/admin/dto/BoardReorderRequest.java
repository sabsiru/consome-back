package consome.interfaces.admin.dto;

import java.util.List;

public record BoardReorderRequest(
        List<OrderItem> orders
) {
    public record OrderItem(Long boardId, int displayOrder) {}
}
