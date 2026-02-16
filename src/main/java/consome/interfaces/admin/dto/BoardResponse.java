package consome.interfaces.admin.dto;


import com.fasterxml.jackson.annotation.JsonProperty;
import consome.domain.admin.Board;
import lombok.AllArgsConstructor;
import lombok.Getter;


@AllArgsConstructor
@Getter
public class BoardResponse {

    private Long id;
    private String name;
    private String description;
    private int displayOrder;
    @JsonProperty("isMain")
    private boolean isMain;

    public static BoardResponse from(Board board) {
        return new BoardResponse(
                board.getId(),
                board.getName(),
                board.getDescription(),
                board.getDisplayOrder(),
                board.isMain()
        );
    }

}
