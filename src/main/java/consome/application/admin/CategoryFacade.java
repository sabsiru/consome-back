package consome.application.admin;

import consome.domain.admin.BoardOrder;
import consome.domain.admin.Category;
import consome.domain.admin.CategoryOrder;
import consome.domain.admin.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

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

    public void reorder(List<CategoryOrder> orders) {
        categoryService.reorder(orders);
    }

    public void delete(Long categoryId) {
        categoryService.delete(categoryId);
    }
}
