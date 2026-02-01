package consome.domain.admin.repository;

import consome.domain.admin.Category;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    boolean existsByNameAndBoardId(String name, Long boardId);

    List<Category> findAllByBoardIdOrderByDisplayOrder(Long boardId);

    List<Category> findAllByDeletedFalseOrderByBoardIdAscDisplayOrderAsc();

    List<Category> findByBoardIdAndDeletedFalseOrderByDisplayOrder(Long boardId);
}
