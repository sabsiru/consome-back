package consome.interfaces.admin.dto;


import consome.domain.board.Board;
import lombok.AllArgsConstructor;
import lombok.Getter;


@AllArgsConstructor
@Getter
public class BoardResponse {

    private Long id;
    private String name;
    private String description;
    private int displayOrder;

    public static BoardResponse from(Board board) {
        return new BoardResponse(
                board.getId(),
                board.getName(),
                board.getDescription(),
                board.getDisplayOrder()
        );
    }

}
