package consome.domain.board;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public Category create(Long boardId, String name, int displayOrder) {
        isNameDuplicate(name);
        Category category = Category.create(boardId, name, displayOrder);
        return categoryRepository.save(category);
    }

    public Category rename(Long categoryId, String newName) {
        Category category = findById(categoryId);
        isNameDuplicate(newName);
        category.rename(newName);
        return categoryRepository.save(category);
    }

    public Category changeOrder(Long categoryId, int newOrder) {
        Category category = findById(categoryId);
        category.changeOrder(newOrder);
        return categoryRepository.save(category);
    }

    public void delete(Long categoryId) {
        Category category = findById(categoryId);
        category.delete();
        categoryRepository.save(category);
    }

    public boolean isNameDuplicate(String name) {
        if (categoryRepository.existsByName(name)) {
            throw new IllegalArgumentException("이미 존재하는 카테고리 이름입니다.");
        }
        if (name == null || name.trim().isEmpty() || name.length() < 1 || name.length() > 10) {
            throw new IllegalArgumentException("카테고리 이름은 1자 이상 10자 이하로 입력해야 합니다.");
        }
        return categoryRepository.existsByName(name);
    }

    public Category findById(Long categoryId) {
        return categoryRepository.findById(categoryId)
                .orElseThrow(() -> new IllegalArgumentException("잘못된 접근입니다."));
    }

    public List<Category> getCategories(Long boardId) {
        return categoryRepository.findAllByBoardIdOrderByDisplayOrder(boardId);
    }
}
