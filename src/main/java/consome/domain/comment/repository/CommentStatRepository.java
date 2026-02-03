package consome.domain.comment.repository;

import consome.domain.comment.CommentStat;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface CommentStatRepository extends JpaRepository<CommentStat, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT cs FROM CommentStat cs WHERE cs.commentId = :commentId")
    Optional<CommentStat> findByCommentIdForUpdate(Long commentId);
}
