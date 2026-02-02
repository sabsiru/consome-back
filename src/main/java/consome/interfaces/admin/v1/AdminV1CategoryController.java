package consome.interfaces.admin.v1;

import consome.application.admin.AdminCategoryFacade;
import consome.domain.admin.Category;
import consome.domain.admin.CategoryOrder;
import consome.interfaces.admin.dto.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/admin/categories")
@Slf4j
public class AdminV1CategoryController {

    private final AdminCategoryFacade adminCategoryFacade;

    @PostMapping()
    public CategoryResponse create(@RequestBody @Valid CreateCategoryRequest request) {
        Category category = adminCategoryFacade.create(request.getBoardId(), request.getName(), request.getDisplayOrder());
        return CategoryResponse.from(category);
    }

    @PatchMapping("/{categoryId}/name")
    public CategoryResponse rename(@PathVariable Long categoryId, @RequestBody RenameRequest request) {
        log.info("Renaming category with ID {} to new name '{}' boardId '{}'", categoryId, request.getName(), request.getBoardId());
        Category category = adminCategoryFacade.rename(categoryId, request.getName(), request.getBoardId());
        return CategoryResponse.from(category);
    }

    @PatchMapping("/{categoryId}/order")
    public CategoryResponse changeOrder(@PathVariable Long categoryId, @RequestBody ChangeOrderRequest request) {
        Category category = adminCategoryFacade.changeOrder(categoryId, request.getNewOrder());
        return CategoryResponse.from(category);
    }

    @PutMapping("/reorder")
    public ResponseEntity<Void> reorder(@RequestBody CategoryReorderRequest request) {
        List<CategoryOrder> orders = request.orders().stream()
                .map(o -> new CategoryOrder(o.boardId(), o.categoryId(), o.displayOrder()))
                .toList();

        adminCategoryFacade.reorder(orders);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{categoryId}")
    public void deleteCategory(@PathVariable Long categoryId) {
        adminCategoryFacade.delete(categoryId);
    }
}
