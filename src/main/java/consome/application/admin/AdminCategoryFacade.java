package consome.application.admin;

import consome.domain.admin.*;
import consome.domain.admin.repository.BoardManagerRepository;
import consome.domain.common.exception.BusinessException;
import consome.domain.user.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminCategoryFacade {

    private final CategoryService categoryService;
    private final BoardManagerRepository boardManagerRepository;

    private void validateManagerAccess(Long boardId, Long userId, Role userRole) {
        if (userRole == Role.ADMIN) return;

        if (!boardManagerRepository.existsByBoardIdAndUserId(boardId, userId)) {
            throw new BusinessException("FORBIDDEN", "해당 게시판을 관리할 권한이 없습니다.");
        }
    }

    public Category create(Long boardId, String name, int displayOrder, Long userId, Role userRole) {
        validateManagerAccess(boardId, userId, userRole);
        return categoryService.create(boardId, name, displayOrder);
    }

    public Category rename(Long categoryId, String name, Long boardId, Long userId, Role userRole) {
        validateManagerAccess(boardId, userId, userRole);
        return categoryService.rename(categoryId, name, boardId);
    }

    public Category changeOrder(Long categoryId, int newOrder, Long userId, Role userRole) {
        Category category = categoryService.findById(categoryId);
        validateManagerAccess(category.getBoardId(), userId, userRole);
        return categoryService.changeOrder(categoryId, newOrder);
    }

    public void reorder(List<CategoryOrder> orders, Long userId, Role userRole) {
        // reorder는 여러 게시판의 카테고리를 한번에 처리할 수 있으므로, 각 boardId에 대해 검증
        List<Long> boardIds = orders.stream()
                .map(CategoryOrder::boardId)
                .distinct()
                .toList();

        for (Long boardId : boardIds) {
            validateManagerAccess(boardId, userId, userRole);
        }

        categoryService.reorder(orders);
    }

    public void delete(Long categoryId, Long userId, Role userRole) {
        Category category = categoryService.findById(categoryId);
        validateManagerAccess(category.getBoardId(), userId, userRole);
        categoryService.delete(categoryId);
    }
}
