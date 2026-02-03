package consome.domain.comment;

import consome.domain.comment.repository.CommentQueryRepository;
import consome.domain.comment.repository.CommentReactionRepository;
import consome.domain.comment.repository.CommentRepository;
import consome.domain.comment.repository.CommentStatRepository;
import consome.domain.post.ReactionType;
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
                    .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 게시글입니다."));
            int newRef = commentQueryRepository.nextRef(postId);

            Comment root = Comment.createRoot(postId, userId, content, newRef);
            CommentStat stat = CommentStat.init(root);
            Comment saved = commentRepository.save(root);
            commentStatRepository.save(stat);
            return saved;
        }

        Comment parent = commentRepository.findById(parentId)
                .orElseThrow(() -> new IllegalArgumentException("잘못된 댓글입니다."));

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
                .orElseThrow(() -> new IllegalArgumentException("잘못된 댓글입니다."));
        if (comment.isDeleted()) {
            throw new IllegalStateException("삭제된 댓글은 수정할 수 없습니다.");
        }
        if (!comment.getUserId().equals(userId)) {
            throw new IllegalStateException("작성자만 댓글을 수정할 수 있습니다.");
        }
        comment.edit(content);
        return commentRepository.save(comment);
    }

    @Transactional
    public Comment delete(Long userId, Long commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("잘못된 댓글입니다."));
        if (!comment.getUserId().equals(userId)) {
            throw new IllegalStateException("작성자만 댓글을 삭제할 수 있습니다.");
        }
        comment.delete();
        return commentRepository.save(comment);
    }

    @Transactional
    public CommentStat like(Long commentId, Long userId) {
        CommentStat stat = getCommentStatForUpdate(commentId);

        if (commentReactionRepository.findByIdForUpdate(commentId, userId, ReactionType.LIKE).isPresent()) {
            throw new IllegalStateException("이미 추천했습니다.");
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
            throw new IllegalStateException("이미 비추천했습니다.");
        }

        CommentReaction reaction = CommentReaction.dislike(commentId, userId);
        stat.increaseDislikeCount();

        commentReactionRepository.save(reaction);
        return commentStatRepository.save(stat);
    }

    @Transactional(readOnly = true)
    public CommentStat getCommentStat(Long commentId) {
        return commentStatRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("댓글 통계를 찾을 수 없습니다."));
    }

    public CommentStat getCommentStatForUpdate(Long commentId) {
        return commentStatRepository.findByCommentIdForUpdate(commentId)
                .orElseThrow(() -> new IllegalArgumentException("댓글 통계를 찾을 수 없습니다."));
    }
}
