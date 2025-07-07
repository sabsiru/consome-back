package consome.application.admin;


import consome.domain.board.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdminFacade {

    private final SectionService sectionService;
    private final BoardService boardService;
    private final CategoryService categoryService;

    public Section createSection(String name, int displayOrder) {
        return sectionService.create(name, displayOrder);
    }

    public Section renameSection(Long sectionId, String newName) {
        return sectionService.rename(sectionId, newName);
    }

    public Section changeSectionOrder(Long sectionId, int newOrder) {
        return sectionService.changeOrder(sectionId, newOrder);
    }

    public void deleteSection(Long sectionId) {
        sectionService.delete(sectionId);
    }

    public Board createBoard(Long sectionId, String name, String description, int displayOrder) {
        return boardService.create(sectionId, name, description, displayOrder);
    }

    public Board renameBoard(Long boardId, String newName) {
        return boardService.rename(boardId, newName);
    }
    public Board changeBoardOrder(Long boardId, int newOrder) {
        return boardService.changeOrder(boardId, newOrder);
    }

    public void deleteBoard(Long boardId) {
        boardService.delete(boardId);
    }

    public Category createCategory(Long boardId, String name, int displayOrder) {
        return categoryService.create(boardId, name, displayOrder);
    }

    public Category renameCategory(Long categoryId, String newName) {
        return categoryService.rename(categoryId, newName);
    }

    public Category changeCategoryOrder(Long categoryId, int newOrder) {
        return categoryService.changeOrder(categoryId, newOrder);
    }

    public void deleteCategory(Long categoryId) {
        categoryService.delete(categoryId);
    }
}
