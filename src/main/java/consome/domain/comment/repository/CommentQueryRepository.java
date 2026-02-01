package consome.domain.comment.repository;

import consome.application.comment.CommentListResult;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CommentQueryRepository{
    Page<CommentListResult> findCommentsByPostId(Long postId, Pageable pageable);
    int allocateReplyStep(Long postId, int parentRef, int parentStep, int parentDepth);

    int nextRef(Long postId);

    Integer findBoundaryStep(Long postId, int ref, int parentStep, int parentDepth);

    int maxStepInRef(Long postId, int ref);

    void shiftStepsFrom(Long postId, int ref, int insertStep);
}
