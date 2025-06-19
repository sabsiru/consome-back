package consome.domain.comment;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    @Modifying
    @Transactional
    @Query("UPDATE Comment c SET c.step = c.step + 1 WHERE c.ref = :ref AND c.step > :step")
    void updateStepsForNewReply(@Param("ref") Long ref, @Param("step") Integer step);

    int findMaxStepByRef(int ref);
}
