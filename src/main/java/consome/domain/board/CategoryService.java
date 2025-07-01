package consome.domain.board;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public Category create(Long sectionId, String name, int displayOrder) {
        Category category = Category.create(sectionId, name, displayOrder);
        return categoryRepository.save(category);
    }

    public Category rename(Long categoryId, String newName) {
        Category category = findById(categoryId);
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

    public Category findById(Long categoryId) {
        return categoryRepository.findById(categoryId)
                .orElseThrow(() -> new IllegalArgumentException("잘못된 접근입니다."));
    }
}
