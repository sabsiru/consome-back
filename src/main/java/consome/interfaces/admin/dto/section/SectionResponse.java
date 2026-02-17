package consome.interfaces.admin.dto.section;

import consome.domain.admin.Board;
import consome.domain.admin.Section;

import java.util.List;

public record SectionResponse(
        Long id,
        String name,
        int displayOrder,
        boolean adminOnly,
        List<BoardSummary> boards
) {
    public static SectionResponse from(Section section, List<Board> boards) {
        return new SectionResponse(
                section.getId(),
                section.getName(),
                section.getDisplayOrder(),
                section.isAdminOnly(),
                boards.stream()
                        .map(BoardSummary::from)
                        .toList()
        );
    }

    public record BoardSummary(
            Long id,
            String name,
            String description
    ) {
        public static BoardSummary from(Board board) {
            return new BoardSummary(
                    board.getId(),
                    board.getName(),
                    board.getDescription()
            );
        }
    }
}
