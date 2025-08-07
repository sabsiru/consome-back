package consome.interfaces.admin.v1;

import consome.application.admin.CategoryFacade;
import consome.domain.board.Category;
import consome.interfaces.admin.dto.CategoryResponse;
import consome.interfaces.admin.dto.ChangeOrderRequest;
import consome.interfaces.admin.dto.CreateCategoryRequest;
import consome.interfaces.admin.dto.RenameRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/admin")
public class AdminV1CategoryController {

    private final CategoryFacade categoryFacade;

    @PostMapping("/boards/{boardId}/categories")
    public CategoryResponse createCategory(@PathVariable Long boardId, @RequestBody @Valid CreateCategoryRequest request) {
        Category category = categoryFacade.create(boardId, request.getName(), request.getDisplayOrder());
        return CategoryResponse.from(category);
    }

    @PatchMapping("/boards/{boardId}/categories/{categoryId}")
    public CategoryResponse renameCategory(@PathVariable Long boardId, @PathVariable Long categoryId, @RequestBody RenameRequest request) {
        Category category = categoryFacade.rename(categoryId, request.getNewName());
        return CategoryResponse.from(category);
    }

    @PatchMapping("/boards/{boardId}/categories/{categoryId}")
    public CategoryResponse changeCategoryOrder(@PathVariable Long boardId, @PathVariable Long categoryId, @RequestBody ChangeOrderRequest request) {
        Category category = categoryFacade.changeOrder(categoryId, request.getNewOrder());
        return CategoryResponse.from(category);
    }

    @DeleteMapping("/categories/{categoryId}")
    public void deleteCategory(@PathVariable Long categoryId) {
        categoryFacade.delete(categoryId);
    }
}
