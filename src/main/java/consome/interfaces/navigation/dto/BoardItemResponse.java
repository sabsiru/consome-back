package consome.interfaces.navigation.dto;

import consome.domain.admin.Board;

import java.util.List;
import java.util.stream.Collectors;

public record BoardItemResponse(
        Long refBoardId,
        String boardName,
        int displayOrder
) {

    public static BoardItemResponse from(Board b) {
        return new BoardItemResponse(b.getId(), b.getName(), b.getDisplayOrder());
    }

    public static List<BoardItemResponse> fromBoards(List<Board> boards) {
        return boards.stream().map(BoardItemResponse::from).collect(Collectors.toList());
    }
}
