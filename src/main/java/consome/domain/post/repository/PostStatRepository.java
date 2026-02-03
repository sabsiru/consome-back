package consome.domain.post.repository;

import consome.domain.post.entity.PostStat;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface PostStatRepository extends JpaRepository<PostStat, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT ps FROM PostStat ps WHERE ps.postId = :postId")
    Optional<PostStat> findByPostIdForUpdate(Long postId);
}
