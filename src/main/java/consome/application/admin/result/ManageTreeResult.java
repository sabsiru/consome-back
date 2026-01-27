package consome.application.admin.result;

import java.util.List;

public record ManageTreeResult(
        List<BoardNode> boards
) {

    public record BoardNode(
            Long id,
            String name,
            String description,
            int displayOrder,
            List<CategoryNode> categories
    ) {}

    public record CategoryNode(
            Long id,
            String name,
            int displayOrder
    ) {}
}
