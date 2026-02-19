package consome.application.post;

import java.util.List;

public record PostPagingResult(
        Long boardId,
        String boardName,
        String description,
        boolean sectionAdminOnly,
        List<PostRowResult> posts,
        List<ManagerInfo> managers,
        int page,
        int size,
        long totalElements,
        int totalPages
) {
    public record ManagerInfo(Long userId, String nickname) {}
}
