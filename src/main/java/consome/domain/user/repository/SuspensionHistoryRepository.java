package consome.domain.user.repository;

import consome.domain.user.SuspensionHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SuspensionHistoryRepository extends JpaRepository<SuspensionHistory, Long> {

    List<SuspensionHistory> findByUserIdOrderByCreatedAtDesc(Long userId);

    int countByUserId(Long userId);
}
