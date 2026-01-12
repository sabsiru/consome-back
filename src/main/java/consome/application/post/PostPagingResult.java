package consome.application.post;

import java.util.List;

public record PostPagingResult(
        Long boardId,
        String boardName,
        List<PostRowResult> posts,
        int page,
        int size,
        long totalElements,
        int totalPages
) {
}
