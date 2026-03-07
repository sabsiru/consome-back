package consome.interfaces.board.dto;

import consome.application.board.FavoriteBoardResult;

public record FavoriteBoardResponse(Long boardId, String name, String description) {
    public static FavoriteBoardResponse from(FavoriteBoardResult result) {
        return new FavoriteBoardResponse(result.boardId(), result.name(), result.description());
    }
}
