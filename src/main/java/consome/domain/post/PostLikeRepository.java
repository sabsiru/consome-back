package consome.domain.post;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface PostLikeRepository  extends JpaRepository<PostLike, Long> {
    List<PostLike> findByPostIdAndType(Long postId, LikeType type);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT pl FROM PostLike pl WHERE pl.postId = :postId AND pl.userId = :userId AND pl.type = :type AND pl.deleted = false")
    Optional<PostLike> findByIdForUpdate(Long postId, Long userId, LikeType type);
}
