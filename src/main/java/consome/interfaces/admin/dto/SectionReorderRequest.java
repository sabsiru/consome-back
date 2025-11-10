package consome.interfaces.admin.dto;

import java.util.List;

public record SectionReorderRequest(
        List<OrderItem> orders
) {
    public record OrderItem(Long sectionId, int displayOrder) {}
}
