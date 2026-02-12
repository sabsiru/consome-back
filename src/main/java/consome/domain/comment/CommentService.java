package consome.domain.comment;

import consome.domain.comment.exception.CommentException;
import consome.domain.comment.repository.CommentQueryRepository;
import consome.domain.comment.repository.CommentReactionRepository;
import consome.domain.comment.repository.CommentRepository;
import consome.domain.comment.repository.CommentStatRepository;
import consome.domain.post.ReactionType;
import consome.domain.post.exception.PostException;
import consome.domain.post.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final CommentReactionRepository commentReactionRepository;
    private final CommentStatRepository commentStatRepository;
    private final CommentQueryRepository commentQueryRepository;
    private final PostRepository postRepository;

    @Transactional
    public Comment comment(Long postId, Long userId, Long parentId, String content) {

        if (parentId == null) {
            postRepository.findByIdForUpdate(postId)
                    .orElseThrow(() -> new PostException.NotFound(postId));
            int newRef = commentQueryRepository.nextRef(postId);

            Comment root = Comment.createRoot(postId, userId, content, newRef);
            CommentStat stat = CommentStat.init(root);
            Comment saved = commentRepository.save(root);
            commentStatRepository.save(stat);
            return saved;
        }

        Comment parent = commentRepository.findById(parentId)
                .orElseThrow(CommentException.NotFound::new);

        int ref = parent.getRef();
        int parentStep = parent.getStep();
        int parentDepth = parent.getDepth();

        commentRepository.lockThreadRoot(postId, ref);

        Integer boundaryStep = commentQueryRepository.findBoundaryStep(postId, ref, parentStep, parentDepth);

        int insertStep;
        if (boundaryStep != null) {
            insertStep = boundaryStep;
        } else {
            int maxStep = commentQueryRepository.maxStepInRef(postId, ref);
            insertStep = maxStep + 1;
        }

        commentQueryRepository.shiftStepsFrom(postId, ref, insertStep);

        Comment reply = Comment.createReply(postId, userId, parent, content, ref, insertStep, parentDepth + 1);
        CommentStat stat = CommentStat.init(reply);
        Comment saved = commentRepository.save(reply);
        commentStatRepository.save(stat);
        return saved;
    }

    @Transactional
    public Comment edit(Long userId, Long commentId, String content) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(CommentException.NotFound::new);
        if (comment.isDeleted()) {
            throw new CommentException.AlreadyDeleted();
        }
        if (!comment.getUserId().equals(userId)) {
            throw new CommentException.Unauthorized("수정");
        }
        comment.edit(content);
        return commentRepository.save(comment);
    }

    @Transactional
    public Comment delete(Long userId, Long commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(CommentException.NotFound::new);
        if (!comment.getUserId().equals(userId)) {
            throw new CommentException.Unauthorized("삭제");
        }
        comment.delete();
        return commentRepository.save(comment);
    }

    @Transactional
    public CommentStat like(Long commentId, Long userId) {
        CommentStat stat = getCommentStatForUpdate(commentId);

        if (commentReactionRepository.findByIdForUpdate(commentId, userId, ReactionType.LIKE).isPresent()) {
            throw new CommentException.AlreadyLiked();
        }

        CommentReaction reaction = CommentReaction.like(commentId, userId);
        stat.increaseLikeCount();

        commentReactionRepository.save(reaction);
        return commentStatRepository.save(stat);
    }

    @Transactional
    public CommentStat dislike(Long commentId, Long userId) {
        CommentStat stat = getCommentStatForUpdate(commentId);

        if (commentReactionRepository.findByIdForUpdate(commentId, userId, ReactionType.DISLIKE).isPresent()) {
            throw new CommentException.AlreadyDisliked();
        }

        CommentReaction reaction = CommentReaction.dislike(commentId, userId);
        stat.increaseDislikeCount();

        commentReactionRepository.save(reaction);
        return commentStatRepository.save(stat);
    }

    @Transactional(readOnly = true)
    public CommentStat getCommentStat(Long commentId) {
        return commentStatRepository.findById(commentId)
                .orElseThrow(() -> new CommentException.StatsNotFound(commentId));
    }

    public CommentStat getCommentStatForUpdate(Long commentId) {
        return commentStatRepository.findByCommentIdForUpdate(commentId)
                .orElseThrow(() -> new CommentException.StatsNotFound(commentId));
    }
}
