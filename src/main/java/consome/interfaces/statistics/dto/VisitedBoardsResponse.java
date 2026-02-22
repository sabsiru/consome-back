package consome.interfaces.statistics.dto;

import consome.application.statistics.VisitedBoardResult;

import java.util.List;

public record VisitedBoardsResponse(
        List<BoardSummary> boards
) {
    public record BoardSummary(
            Long id,
            String name
    ) {
        public static BoardSummary from(VisitedBoardResult result) {
            return new BoardSummary(result.id(), result.name());
        }
    }

    public static VisitedBoardsResponse from(List<VisitedBoardResult> results) {
        return new VisitedBoardsResponse(
                results.stream()
                        .map(BoardSummary::from)
                        .toList()
        );
    }
}
