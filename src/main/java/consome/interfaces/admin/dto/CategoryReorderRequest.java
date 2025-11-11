package consome.interfaces.admin.dto;

import java.util.List;

public record CategoryReorderRequest(
        List<OrderItem> orders
) {
    public record OrderItem(Long categoryId, int displayOrder) {}
}
