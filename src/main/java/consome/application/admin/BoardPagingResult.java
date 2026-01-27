package consome.application.admin;

import java.util.List;

public record BoardPagingResult(
        List<BoardSearchResult> boards,
        int page,
        int size,
        long totalElements,
        int totalPages
) {
}
