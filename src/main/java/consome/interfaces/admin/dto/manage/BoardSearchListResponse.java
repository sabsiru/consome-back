package consome.interfaces.admin.dto.manage;

import consome.application.admin.BoardPagingResult;

import java.util.List;

public record BoardSearchListResponse(
        List<BoardSearchResponse> boards,
        int page,
        int size,
        long totalElements,
        int totalPages
) {
    public static BoardSearchListResponse from(BoardPagingResult result) {
        List<BoardSearchResponse> boards = result.boards().stream()
                .map(BoardSearchResponse::from)
                .toList();

        return new BoardSearchListResponse(
                boards,
                result.page(),
                result.size(),
                result.totalElements(),
                result.totalPages()
        );
    }
}
