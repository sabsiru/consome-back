package consome.interfaces.admin.v1;


import consome.application.admin.SectionFacade;
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

    private final SectionFacade sectionFacade;

    @PostMapping("/sections")
    public SectionResponse createSection(@RequestBody @Valid CreateSectionRequest request) {
        Section section = sectionFacade.createSection(request.getName(), request.getDisplayOrder());
        return SectionResponse.from(section);
    }

    @PatchMapping("/sections/{sectionId}")
    public SectionResponse renameSection(@PathVariable Long sectionId, @RequestBody RenameRequest request) {
        Section section = sectionFacade.renameSection(sectionId, request.getNewName());
        return SectionResponse.from(section);
    }

    @PatchMapping("/sections/{sectionId}")
    public SectionResponse changeSectionOrder(@PathVariable Long sectionId, @RequestBody ChangeOrderRequest request) {
        Section section = sectionFacade.changeSectionOrder(sectionId, request.getNewOrder());
        return SectionResponse.from(section);
    }

    @DeleteMapping("/sections/{sectionId}")
    public void deleteSection(@PathVariable Long sectionId) {
        sectionFacade.deleteSection(sectionId);
    }

    @PostMapping("/boards")
    public BoardResponse createBoard(@RequestBody @Valid CreateBoardRequest request) {
        Board board = sectionFacade.createBoard(request.getSectionId(), request.getName(), request.getDescription(), request.getDisplayOrder());
        return BoardResponse.from(board);
    }

    @PatchMapping("/boards/{boardId}")
    public BoardResponse renameBoard(@PathVariable Long boardId, @RequestBody RenameRequest request) {
        Board board = sectionFacade.renameBoard(boardId, request.getNewName());
        return BoardResponse.from(board);
    }

    @PatchMapping("/boards/{boardId}")
    public BoardResponse changeBoardOrder(@PathVariable Long boardId, @RequestBody ChangeOrderRequest request) {
        Board board = sectionFacade.changeBoardOrder(boardId, request.getNewOrder());
        return BoardResponse.from(board);
    }

    @DeleteMapping("/boards/{boardId}")
    public void deleteBoard(@PathVariable Long boardId) {
        sectionFacade.deleteBoard(boardId);
    }

    @PostMapping("/boards/{boardId}/categories")
    public CategoryResponse createCategory(@PathVariable Long boardId, @RequestBody @Valid CreateCategoryRequest request) {
        Category category = sectionFacade.createCategory(boardId, request.getName(), request.getDisplayOrder());
        return CategoryResponse.from(category);
    }

    @PatchMapping("/boards/{boardId}/categories/{categoryId}")
    public CategoryResponse renameCategory(@PathVariable Long boardId, @PathVariable Long categoryId, @RequestBody RenameRequest request) {
        Category category = sectionFacade.renameCategory(categoryId, request.getNewName());
        return CategoryResponse.from(category);
    }

    @PatchMapping("/boards/{boardId}/categories/{categoryId}")
    public CategoryResponse changeCategoryOrder(@PathVariable Long boardId, @PathVariable Long categoryId, @RequestBody ChangeOrderRequest request) {
        Category category = sectionFacade.changeCategoryOrder(categoryId, request.getNewOrder());
        return CategoryResponse.from(category);
    }

    @DeleteMapping("/categories/{categoryId}")
    public void deleteCategory(@PathVariable Long categoryId) {
        sectionFacade.deleteCategory(categoryId);
    }

}