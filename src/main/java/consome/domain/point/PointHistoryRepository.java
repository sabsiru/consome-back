package consome.domain.point;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PointHistoryRepository extends JpaRepository<PointHistory, Long> {
    List<PointHistory> findByUserIdOrderByCreatedAtDesc(Long userId);

    Optional<PointHistory> findFirstByUserIdOrderByCreatedAtDesc(Long userId);
}
