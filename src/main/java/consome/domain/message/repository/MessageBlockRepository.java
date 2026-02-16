package consome.domain.message.repository;

import consome.domain.message.entity.MessageBlock;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MessageBlockRepository extends JpaRepository<MessageBlock, Long> {

    boolean existsByBlockerIdAndBlockedId(Long blockerId, Long blockedId);

    Optional<MessageBlock> findByBlockerIdAndBlockedId(Long blockerId, Long blockedId);

    Page<MessageBlock> findByBlockerId(Long blockerId, Pageable pageable);
}
