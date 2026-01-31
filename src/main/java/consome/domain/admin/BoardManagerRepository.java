package consome.domain.admin;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BoardManagerRepository extends JpaRepository<BoardManager, Long> {
    List<BoardManager> findByBoardId(Long boardId);
    List<BoardManager> findByUserId(Long userId);
    boolean existsByUserId(Long userId);
    boolean existsByBoardIdAndUserId(Long boardId, Long userId);
    Optional<BoardManager> findByBoardIdAndUserId(Long boardId, Long userId);
    void deleteByBoardIdAndUserId(Long boardId, Long userId);
}
