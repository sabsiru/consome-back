package consome.interfaces.admin.dto.manage;

import com.fasterxml.jackson.annotation.JsonProperty;
import consome.application.admin.BoardSearchResult;

public record BoardSearchResponse(
        Long id,
        String name,
        String description,
        int displayOrder,
        @JsonProperty("isMain") boolean isMain
) {
    public static BoardSearchResponse from(BoardSearchResult result) {
        return new BoardSearchResponse(
                result.id(),
                result.name(),
                result.description(),
                result.displayOrder(),
                result.isMain()
        );
    }
}
