package consome.domain.post.repository;

import consome.domain.post.entity.Post;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface PostRepository extends JpaRepository<Post, Long> {
    @Query("SELECT p FROM Post p WHERE p.id = :postId AND p.deleted = false")
    Optional<Post> findByPostIdAndDeletedFalse(Long postId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT p FROM Post p WHERE p.id = :postId")
    Optional<Post> findByIdForUpdate(Long postId);

    @Query("SELECT p FROM Post p WHERE p.boardId = :boardId AND p.isPinned = true AND p.deleted = false ORDER BY p.pinnedOrder")
    List<Post> findByBoardIdAndIsPinnedTrue(Long boardId);
}
