package consome.interfaces.board.dto;

import consome.application.board.UserBoardSearchResult;

public record BoardSearchResponse(Long id, String name) {
    public static BoardSearchResponse from(UserBoardSearchResult result) {
        return new BoardSearchResponse(result.id(), result.name());
    }
}
