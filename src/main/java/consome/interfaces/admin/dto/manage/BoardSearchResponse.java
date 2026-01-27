package consome.interfaces.admin.dto.manage;

import consome.application.admin.BoardSearchResult;

public record BoardSearchResponse(
        Long id,
        String name,
        String description,
        int displayOrder
) {
    public static BoardSearchResponse from(BoardSearchResult result) {
        return new BoardSearchResponse(
                result.id(),
                result.name(),
                result.description(),
                result.displayOrder()
        );
    }
}
