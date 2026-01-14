package consome.domain.comment;

import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    @Modifying
    @Transactional
    @Query("UPDATE Comment c SET c.step = c.step + 1 WHERE c.postId = :postId AND c.ref = :ref AND c.step > :step")
    void updateStepsOtherReply(@Param("postId") Long postId, @Param("ref") int ref, @Param("step") int step);

    List<Comment> findByPostIdOrderByRefAscStepAsc(Long postId);

    @Query("SELECT MAX(c.step) FROM Comment c WHERE c.parentId = :parentId")
    Optional<Integer> findMaxStepByParentId(@Param("parentId") Long parentId);

    Page<Comment> findByPostId(Long postId, Pageable pageable);

    @Query("select c.parentId from Comment c where c.id = :parentId")
    Long findParentIdOfParentId(Long parentId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
select c from Comment c
where c.postId = :postId
  and c.ref = :ref
  and c.parentId is null
""")
    Optional<Comment> lockThreadRoot(@Param("postId") Long postId, @Param("ref") int ref);
}