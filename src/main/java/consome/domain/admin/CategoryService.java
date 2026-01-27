package consome.domain.admin;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public Category create(Long boardId, String name, int displayOrder) {
        isNameDuplicate(name, boardId);
        Category category = Category.create(boardId, name, displayOrder);
        return categoryRepository.save(category);
    }

    public Category rename(Long categoryId, String name, Long boardId) {
        isNameDuplicate(name, boardId);
        Category category = findById(categoryId);
        category.rename(name);
        return categoryRepository.save(category);
    }

    public Category changeOrder(Long categoryId, int newOrder) {
        Category category = findById(categoryId);
        category.changeOrder(newOrder);
        return categoryRepository.save(category);
    }

    @Transactional
    public void reorder(List<CategoryOrder> orders) {
       List<Long> boardIds = orders.stream()
                .map(CategoryOrder::boardId)
                .distinct()
                .toList();

        for (Long boardId : boardIds) {
            reorderByBoardId(boardId, orders.stream()
                    .filter(o -> o.boardId().equals(boardId))
                    .toList());
        }
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void reorderByBoardId(Long boardId, List<CategoryOrder> boardOrders) {
        List<Category> categories = categoryRepository.findByBoardIdAndDeletedFalseOrderByDisplayOrder(boardId);

        // 1️⃣ 임시 음수화
        for (Category category : categories) {
            category.changeOrder(-category.getDisplayOrder());
        }
        categoryRepository.flush();

        // 2️⃣ 실제 순서 반영
        for (CategoryOrder order : boardOrders) {
            Category category = categoryRepository.findById(order.categoryId())
                    .orElseThrow(() -> new IllegalArgumentException("잘못된 접근입니다."));
            category.changeOrder(order.displayOrder());
        }
        categoryRepository.flush();
    }

    public void delete(Long categoryId) {
        Category category = findById(categoryId);
        category.delete();
        categoryRepository.save(category);
    }

    public boolean isNameDuplicate(String name, Long boardId) {
        if (categoryRepository.existsByNameAndBoardId(name, boardId)) {
            throw new IllegalArgumentException("이미 존재하는 카테고리 이름입니다.");
        }
        if (name == null || name.trim().isEmpty() || name.length() < 1 || name.length() > 10) {
            throw new IllegalArgumentException("카테고리 이름은 1자 이상 10자 이하로 입력해야 합니다.");
        }
        return categoryRepository.existsByNameAndBoardId(name, boardId);
    }

    public Category findById(Long categoryId) {
        return categoryRepository.findById(categoryId)
                .orElseThrow(() -> new IllegalArgumentException("categoryId에 해당하는 카테고리가 존재하지 않습니다."));
    }

    public List<Category> findAllOrderedByBoard(Long boardId) {
        return categoryRepository.findAllByBoardIdOrderByDisplayOrder(boardId);
    }

    public List<Category> findAllOrdered() {
        return categoryRepository.findAllByDeletedFalseOrderByBoardIdAscDisplayOrderAsc();
    }
}
