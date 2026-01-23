package consome.interfaces.admin.v1;

import consome.application.admin.ManageBoardFacade;
import consome.domain.admin.Board;
import consome.domain.admin.BoardOrder;
import consome.interfaces.admin.dto.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/admin/boards")
public class AdminV1BoardController {

    private final ManageBoardFacade manageBoardFacade;

    @PostMapping()
    public BoardResponse create(@RequestBody @Valid CreateBoardRequest request) {
        Board board = manageBoardFacade.create(request.getSectionId(), request.getName(), request.getDescription(), request.getDisplayOrder());
        return BoardResponse.from(board);
    }

    @PatchMapping("/{boardId}")
    public BoardResponse update(@PathVariable Long boardId,
                                @RequestBody @Valid UpdateBoardRequest request) {
        Board board = manageBoardFacade.update(boardId, request.name(), request.description());
        return BoardResponse.from(board);
    }

    @PatchMapping("/{boardId}/name")
    public BoardResponse rename(@PathVariable Long boardId, @RequestBody RenameRequest request) {
        Board board = manageBoardFacade.rename(boardId, request.getNewName());
        return BoardResponse.from(board);
    }

    @PatchMapping("/{boardId}/order")
    public BoardResponse changeOrder(@PathVariable Long boardId, @RequestBody ChangeOrderRequest request) {
        Board board = manageBoardFacade.changeOrder(boardId, request.getNewOrder());
        return BoardResponse.from(board);
    }

    @PutMapping("/reorder")
    public ResponseEntity<Void> reorder(@RequestBody BoardReorderRequest request) {
        List<BoardOrder> orders = request.orders().stream()
                .map(o -> new BoardOrder(o.sectionId(), o.boardId(), o.displayOrder()))
                .toList();

        manageBoardFacade.reorder(orders);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{boardId}")
    public void delete(@PathVariable Long boardId) {
        manageBoardFacade.delete(boardId);
    }

}