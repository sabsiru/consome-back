package consome.application.admin.result;

import java.util.List;

public record ManageTreeResult(
        List<SectionNode> sections
) {

    public record SectionNode(
            Long id,
            String name,
            int displayOrder,
            List<BoardNode> boards
    ) {}

    public record BoardNode(
            Long id,
            String name,
            int displayOrder,
            List<CategoryNode> categories
    ) {}

    public record CategoryNode(
            Long id,
            String name,
            int displayOrder
    ) {}
}