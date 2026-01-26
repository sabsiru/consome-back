package consome.domain.comment;

import consome.domain.post.ReactionType;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface CommentReactionRepository extends JpaRepository<CommentReaction, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT cr FROM CommentReaction cr WHERE cr.commentId = :commentId AND cr.userId = :userId AND cr.type = :type AND cr.deleted = false")
    Optional<CommentReaction> findByIdForUpdate(Long commentId, Long userId, ReactionType type);

    Optional<CommentReaction> findByCommentIdAndUserIdAndDeletedFalse(Long commentId, Long userId);

    Optional<CommentReaction>findByCommentIdAndUserIdAndTypeAndDeletedFalse(Long commentId, Long userId, ReactionType type);

}
