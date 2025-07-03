package consome.domain.board;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SectionBoardInfo {
    private Long sectionId;
    private String sectionName;
    private int sectionOrder;
    private Long boardId;
    private String boardName;
    private int boardOrder;
}
