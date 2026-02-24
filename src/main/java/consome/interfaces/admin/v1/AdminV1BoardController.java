package consome.interfaces.admin.v1;

import consome.application.admin.AdminBoardFacade;
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

    private final AdminBoardFacade adminBoardFacade;

    @PostMapping()
    public BoardResponse create(@RequestBody @Valid CreateBoardRequest request) {
        Board board = adminBoardFacade.create(request.getName(), request.getDescription(), request.getSectionId());
        return BoardResponse.from(board);
    }

    @PatchMapping("/{boardId}")
    public BoardResponse update(@PathVariable Long boardId,
                                @RequestBody @Valid UpdateBoardRequest request) {
        Board board = adminBoardFacade.update(boardId, request.name(), request.description());
        return BoardResponse.from(board);
    }

    @PutMapping("/main/reorder")
    public ResponseEntity<Void> reorderMainBoards(@RequestBody BoardReorderRequest request) {
        List<BoardOrder> orders = request.orders().stream()
                .map(o -> new BoardOrder(o.boardId(), o.displayOrder()))
                .toList();

        adminBoardFacade.reorderMainBoards(orders);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{boardId}")
    public void delete(@PathVariable Long boardId) {
        adminBoardFacade.delete(boardId);
    }

    @PatchMapping("/{boardId}/main")
    public BoardResponse toggleMain(@PathVariable Long boardId) {
        Board board = adminBoardFacade.toggleMain(boardId);
        return BoardResponse.from(board);
    }

    @GetMapping("/{boardId}/categories")
    public ResponseEntity<List<CategoryResponse>> findAllOrderedByBoard(@PathVariable Long boardId) {
        List<CategoryResponse> categories = adminBoardFacade.findAllOrderedByBoard(boardId).stream()
                .map(CategoryResponse::from)
                .toList();
        return ResponseEntity.ok(categories);
    }

    @PatchMapping("/{boardId}/write-enabled")
    public BoardResponse toggleWriteEnabled(@PathVariable Long boardId) {
        Board board = adminBoardFacade.toggleWriteEnabled(boardId);
        return BoardResponse.from(board);
    }

    @PatchMapping("/{boardId}/comment-enabled")
    public BoardResponse toggleCommentEnabled(@PathVariable Long boardId) {
        Board board = adminBoardFacade.toggleCommentEnabled(boardId);
        return BoardResponse.from(board);
    }
}
