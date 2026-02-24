package consome.interfaces.navigation.dto;

import consome.application.navigation.BoardResult;
import consome.application.navigation.FeaturedBoardsResult;

import java.util.List;

public record FeaturedBoardsResponse(
        List<BoardItem> pinnedBoards,
        List<BoardItem> popularBoards
) {
    public record BoardItem(
            Long boardId,
            String boardName
    ) {
        public static BoardItem from(BoardResult result) {
            return new BoardItem(result.boardId(), result.boardName());
        }
    }

    public static FeaturedBoardsResponse from(FeaturedBoardsResult result) {
        return new FeaturedBoardsResponse(
                result.pinnedBoards().stream().map(BoardItem::from).toList(),
                result.popularBoards().stream().map(BoardItem::from).toList()
        );
    }
}
