package consome.interfaces.admin.v1;


import consome.application.admin.AdminFacade;
import consome.domain.board.Board;
import consome.domain.board.Category;
import consome.domain.board.Section;
import consome.interfaces.admin.dto.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/admin")
public class AdminV1Controller {

    private final AdminFacade adminFacade;

    @PostMapping("/sections")
    public SectionResponse createSection(@RequestBody @Valid CreateSectionRequest request) {
        Section section = adminFacade.createSection(request.getName(), request.getDisplayOrder());
        return SectionResponse.from(section);
    }

    @PutMapping("/sections/{sectionId}/name")
    public SectionResponse renameSection(@PathVariable Long sectionId, @RequestBody RenameRequest request) {
        Section section = adminFacade.renameSection(sectionId, request.getNewName());
        return SectionResponse.from(section);
    }

    @PutMapping("/sections/{sectionId}/order")
    public SectionResponse changeSectionOrder(@PathVariable Long sectionId, @RequestBody ChangeOrderRequest request) {
        Section section = adminFacade.changeSectionOrder(sectionId, request.getNewOrder());
        return SectionResponse.from(section);
    }

    @DeleteMapping("/sections/{sectionId}")
    public void deleteSection(@PathVariable Long sectionId) {
        adminFacade.deleteSection(sectionId);
    }

    @PostMapping("/boards")
    public BoardResponse createBoard(@RequestBody @Valid CreateBoardRequest request) {
        Board board = adminFacade.createBoard(request.getSectionId(), request.getName(), request.getDescription(), request.getDisplayOrder());
        return BoardResponse.from(board);
    }

    @PostMapping("/boards/{boardId}/name")
    public BoardResponse renameBoard(@PathVariable Long boardId, @RequestBody RenameRequest request) {
        Board board = adminFacade.renameBoard(boardId, request.getNewName());
        return BoardResponse.from(board);
    }

    @PostMapping("/boards/{boardId}/order")
    public BoardResponse changeBoardOrder(@PathVariable Long boardId, @RequestBody ChangeOrderRequest request) {
        Board board = adminFacade.changeBoardOrder(boardId, request.getNewOrder());
        return BoardResponse.from(board);
    }

    @DeleteMapping("/boards/{boardId}")
    public void deleteBoard(@PathVariable Long boardId) {
        adminFacade.deleteBoard(boardId);
    }

    @PostMapping("/categories")
    public CategoryResponse createCategory(@RequestBody @Valid CreateCategoryReqeust request) {
        Category category = adminFacade.createCategory(request.getBoardId(), request.getName(), request.getDisplayOrder());
        return CategoryResponse.from(category);
    }

    @PostMapping("/categories/{categoryId}/name")
    public CategoryResponse renameCategory(@PathVariable Long categoryId, String newName) {
        Category category = adminFacade.renameCategory(categoryId, newName);
        return CategoryResponse.from(category);
    }

    @PostMapping("/categories/{categoryId}/order")
    public CategoryResponse changeCategoryOrder(@PathVariable Long categoryId, int newOrder) {
        Category category = adminFacade.changeCategoryOrder(categoryId, newOrder);
        return CategoryResponse.from(category);
    }

    @DeleteMapping("/categories/{categoryId}")
    public void deleteCategory(@PathVariable Long categoryId) {
        adminFacade.deleteCategory(categoryId);
    }

}