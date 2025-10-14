package consome.domain.comment;

import consome.domain.post.ReactionType;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final CommentReactionRepository commentReactionRepository;
    private final CommentQueryRepository commentQueryRepository;

    @Transactional
    public Comment comment(Long postId, Long userId, Long parentId, String content) {

        if (parentId == null) {
            Comment comment = Comment.reply(postId, userId, null, content, 0);
            commentRepository.save(comment);
            return comment;
        } else {
            Comment parentComment = commentRepository.findById(parentId)
                    .orElseThrow(() -> new IllegalArgumentException("잘못된 댓글입니다."));

            int maxStep = commentRepository.findMaxStepByParentId(parentId)
                    .orElse(0);

            Comment comment = Comment.reply(postId, userId, parentComment, content, maxStep);
            commentRepository.updateStepsOtherReply(postId, parentComment.getRef(), comment.getStep());
            commentRepository.save(comment);
            return comment;
        }
    }

    @Transactional
    public Comment edit(Long commentId, Long userId, String content) {
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

    @Transactional(readOnly = true)
    public List<Comment> findByPostIdOrderByRefAscStepAsc(Long postId) {
        return commentRepository.findByPostIdOrderByRefAscStepAsc(postId).stream()
                .map(comment -> new Comment(
                        comment.getPostId(),
                        comment.getUserId(),
                        comment.getParentId(),
                        comment.getRef(),
                        comment.getStep(),
                        comment.getDepth(),
                        comment.isDeleted() ? "삭제된 댓글입니다." : comment.getContent()
                ))
                .toList();
    }

    @Transactional
    public Comment delete(Long commentId, Long userId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("잘못된 댓글입니다."));
        if (!comment.getUserId().equals(userId)) {
            throw new IllegalStateException("작성자만 댓글을 삭제할 수 있습니다.");
        }
        comment.delete();
        Comment save = commentRepository.save(comment);

        return save;
    }

    @Transactional
    public CommentReaction like(Long commentId, Long userId) {
        ReactionType reactionType = getReaction(commentId, userId);
        if (reactionType == null) {
            CommentReaction reaction = CommentReaction.like(commentId, userId);
            return commentReactionRepository.save(reaction);
        } else if (reactionType == ReactionType.LIKE) {
            throw new IllegalStateException("이미 좋아요를 눌렀습니다.");
        } else {
            commentReactionRepository.deleteByCommentIdAndUserId(commentId, userId);
            CommentReaction reaction = CommentReaction.like(commentId, userId);
            return commentReactionRepository.save(reaction);
        }
    }

    @Transactional
    public CommentReaction dislike(Long commentId, Long userId) {
        ReactionType reactionType = getReaction(commentId, userId);
        if (reactionType == null) {
            CommentReaction reaction = CommentReaction.dislike(commentId, userId);
            return commentReactionRepository.save(reaction);
        } else if (reactionType == ReactionType.DISLIKE) {
            throw new IllegalStateException("이미 싫어요를 눌렀습니다.");
        } else {
            commentReactionRepository.deleteByCommentIdAndUserId(commentId, userId);
            CommentReaction reaction = CommentReaction.dislike(commentId, userId);
            return commentReactionRepository.save(reaction);
        }
    }

    @Transactional
    public CommentReaction toggleReaction(Long commentId, Long userId, ReactionType type) {
        ReactionType currentReaction = getReaction(commentId, userId);

        if (currentReaction == type.NONE) {
            // 반응 없음 → 새로운 반응 추가
            return type == ReactionType.LIKE ? like(commentId, userId) : dislike(commentId, userId);
        }

        if (currentReaction == type) {
            // 같은 반응 → 취소
            return cancel(commentId, userId);
        }

        // 다른 반응 → 교체
        cancel(commentId, userId);
        return type == ReactionType.LIKE ? like(commentId, userId) : dislike(commentId, userId);
    }

    @Transactional
    public ReactionType getReaction(Long commentId, Long userId) {
        Optional<CommentReaction> reaction = commentReactionRepository.findByCommentIdAndUserId(commentId, userId);
        if (reaction.isPresent()) {
            return reaction.get().getType();
        }
        return ReactionType.NONE;
    }

    @Transactional
    public CommentReaction cancel(Long commentId, Long userId) {
        Optional<CommentReaction> reaction = commentReactionRepository.findByCommentIdAndUserId(commentId, userId);
        if (reaction.isEmpty()) {
            throw new IllegalStateException("좋아요나 싫어요를 누르지 않았습니다.");
        }
        commentReactionRepository.deleteByCommentIdAndUserId(commentId, userId);

        return null;
    }

    @Transactional
    public long countReactions(Long commentId, ReactionType type) {
        return commentReactionRepository.countByCommentIdAndType(commentId, type);
    }

    @Transactional(readOnly = true)
    public Page<Comment> listByPost(Long postId, Pageable pageable) {
        return commentQueryRepository.findCommentsByPostId(postId, pageable);
    }

}