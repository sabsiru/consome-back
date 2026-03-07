package consome.domain.board.repository;

import consome.domain.board.entity.BoardFavorite;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BoardFavoriteRepository extends JpaRepository<BoardFavorite, Long> {
    Optional<BoardFavorite> findByUserIdAndBoardId(Long userId, Long boardId);
    boolean existsByUserIdAndBoardId(Long userId, Long boardId);
    List<BoardFavorite> findByUserId(Long userId);
}
