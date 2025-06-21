package consome.domain.post;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface PostReactionRepository extends JpaRepository<PostReaction, Long> {
    List<PostReaction> findByPostIdAndType(Long postId, ReactionType type);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT pl FROM PostReaction pl WHERE pl.postId = :postId AND pl.userId = :userId AND pl.type = :type AND pl.deleted = false")
    Optional<PostReaction> findByIdForUpdate(Long postId, Long userId, ReactionType type);
}
