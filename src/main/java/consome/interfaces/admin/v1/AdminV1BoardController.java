package consome.interfaces.admin.v1;

import consome.application.admin.BoardFacade;
import consome.domain.board.Board;
import consome.interfaces.admin.dto.BoardResponse;
import consome.interfaces.admin.dto.ChangeOrderRequest;
import consome.interfaces.admin.dto.CreateBoardRequest;
import consome.interfaces.admin.dto.RenameRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/admin/boards")
public class AdminV1BoardController {

    private final BoardFacade boardFacade;

    @PostMapping()
    public BoardResponse create(@RequestBody @Valid CreateBoardRequest request) {
        Board board = boardFacade.create(request.getRefSectionId(), request.getName(), request.getDescription(), request.getDisplayOrder());
        return BoardResponse.from(board);
    }

    @PatchMapping("/{boardId}/name")
    public BoardResponse rename(@PathVariable Long boardId, @RequestBody RenameRequest request) {
        Board board = boardFacade.rename(boardId, request.getNewName());
        return BoardResponse.from(board);
    }

    @PatchMapping("/{boardId}/order")
    public BoardResponse changeOrder(@PathVariable Long boardId, @RequestBody ChangeOrderRequest request) {
        Board board = boardFacade.changeOrder(boardId, request.getNewOrder());
        return BoardResponse.from(board);
    }

    @DeleteMapping("/{boardId}")
    public void delete(@PathVariable Long boardId) {
        boardFacade.delete(boardId);
    }
}