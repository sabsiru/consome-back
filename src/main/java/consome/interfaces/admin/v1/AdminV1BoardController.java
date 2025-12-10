package consome.interfaces.admin.v1;

import consome.application.admin.BoardFacade;
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

    private final BoardFacade boardFacade;

    @PostMapping()
    public BoardResponse create(@RequestBody @Valid CreateBoardRequest request) {
        Board board = boardFacade.create(request.getSectionId(), request.getName(), request.getDescription(), request.getDisplayOrder());
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

    @PutMapping("/reorder")
    public ResponseEntity<Void> reorder(@RequestBody BoardReorderRequest request) {
        List<BoardOrder> orders = request.orders().stream()
                .map(o -> new BoardOrder(o.sectionId(), o.boardId(), o.displayOrder()))
                .toList();

        boardFacade.reorder(orders);
        return ResponseEntity.ok().build();
    }


    @DeleteMapping("/{boardId}")
    public void delete(@PathVariable Long boardId) {
        boardFacade.delete(boardId);
    }
}