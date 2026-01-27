package consome.application.admin;

import consome.domain.admin.*;
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

    public Category rename(Long categoryId, String name, Long boardId) {

        return categoryService.rename(categoryId, name, boardId);
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
