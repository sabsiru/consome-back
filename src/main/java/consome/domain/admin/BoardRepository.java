package consome.domain.admin;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BoardRepository extends JpaRepository<Board, Long> {
    boolean existsByName(String name);

    List<Board> findByDeletedFalseOrderByDisplayOrder();
}
