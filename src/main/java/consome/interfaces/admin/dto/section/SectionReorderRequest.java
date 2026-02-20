package consome.interfaces.admin.dto.section;

import java.util.List;

public record SectionReorderRequest(
        List<OrderItem> orders
) {
    public record OrderItem(Long sectionId, int displayOrder) {}
}
