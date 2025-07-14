package consome.interfaces.admin;


import consome.application.admin.AdminFacade;
import consome.domain.board.Board;
import consome.domain.board.Category;
import consome.domain.board.Section;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin")
public class AdminController {

    private final AdminFacade adminFacade;

    @PostMapping("/sections")
    public Section createSection(String name, int displayOrder) {
        return adminFacade.createSection(name, displayOrder);
    }

    @PostMapping("/sections/rename")
    public Section renameSection(Long sectionId, String newName) {
        return adminFacade.renameSection(sectionId, newName);
    }

    @PostMapping("/sections/change-order")
    public Section changeSectionOrder(Long sectionId, int newOrder) {
        return adminFacade.changeSectionOrder(sectionId, newOrder);
    }

    @DeleteMapping("/sections/{sectionId}")
    public void deleteSection(@PathVariable Long sectionId) {
        adminFacade.deleteSection(sectionId);
    }

    @PostMapping("/boards")
    public Board createBoard(Long sectionId, String name, String description, int displayOrder) {
        return adminFacade.createBoard(sectionId, name, description, displayOrder);
    }

    @PostMapping("/boards/rename")
    public Board renameBoard(Long boardId, String newName) {
        return adminFacade.renameBoard(boardId, newName);
    }

    @PostMapping("/boards/change-order")
    public Board changeBoardOrder(Long boardId, int newOrder) {
        return adminFacade.changeBoardOrder(boardId, newOrder);
    }

    @DeleteMapping("/boards/{boardId}")
    public void deleteBoard(@PathVariable Long boardId) {
        adminFacade.deleteBoard(boardId);
    }

    @PostMapping("/categories")
    public Category createCategory(Long boardId, String name, int displayOrder) {
        return adminFacade.createCategory(boardId, name, displayOrder);
    }

    @PostMapping("/categories/rename")
    public Category renameCategory(Long categoryId, String newName) {
        return adminFacade.renameCategory(categoryId, newName);
    }

    @PostMapping("/categories/change-order")
    public Category changeCategoryOrder(Long categoryId, int newOrder) {
        return adminFacade.changeCategoryOrder(categoryId, newOrder);
    }

    @DeleteMapping("/categories/{categoryId}")
    public void deleteCategory(@PathVariable Long categoryId) {
        adminFacade.deleteCategory(categoryId);
    }

}