package consome.domain.post;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface PostViewRepository extends JpaRepository<PostView, Long> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT pv FROM PostView pv WHERE pv.postId = :postId AND (pv.userId = :userId OR pv.userIp = :userIp)")
    Optional<PostView> findByPostIdAndUserIdOrUserIp(Long postId, Long userId, String userIp);
}
