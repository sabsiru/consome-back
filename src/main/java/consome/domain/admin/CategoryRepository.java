package consome.domain.admin;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    boolean existsByName(String name);

    List<Category> findAllByBoardIdOrderByDisplayOrder(Long boardId);

    List<Category> findAllByDeletedFalseOrderByBoardIdAscDisplayOrderAsc();

    List<Category> findByBoardIdAndDeletedFalseOrderByDisplayOrder(Long boardId);
}
