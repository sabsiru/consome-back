package consome.interfaces.admin.dto;

import java.util.List;

public record PinnedPostReorderRequest(
        Long boardId,
        List<PinnedPostOrder> orders
) {
    public record PinnedPostOrder(
            Long postId,
            Integer pinnedOrder
    ) {}
}
