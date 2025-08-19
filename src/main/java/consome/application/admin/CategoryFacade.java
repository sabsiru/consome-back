package consome.application.admin;

import consome.domain.board.Category;
import consome.domain.board.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CategoryFacade {

    private final CategoryService categoryService;

    public Category create(Long boardId, String name, int displayOrder) {
        return categoryService.create(boardId, name, displayOrder);
    }

    public Category rename(Long categoryId, String newName) {
        return categoryService.rename(categoryId, newName);
    }

    public Category changeOrder(Long categoryId, int newOrder) {
        return categoryService.changeOrder(categoryId, newOrder);
    }

    public void delete(Long categoryId) {
        categoryService.delete(categoryId);
    }
}
