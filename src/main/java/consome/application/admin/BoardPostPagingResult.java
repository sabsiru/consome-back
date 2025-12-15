package consome.application.admin;

import java.util.List;

public record BoardPostPagingResult(
        List<BoardPostRowResult> posts,
        int page,
        int size,
        long totalElements,
        int totalPages
) {
}
