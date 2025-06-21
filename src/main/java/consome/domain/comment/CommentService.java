package consome.domain.comment;

import consome.domain.post.ReactionType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final CommentReactionRepository commentReactionRepository;

    @Transactional
    public Comment reply(Long postId, Long userId, Long parentId, String content) {

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
    public void delete(Long commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("잘못된 댓글입니다."));
        comment.delete();
        commentRepository.save(comment);
    }

    @Transactional
    public CommentReaction like(Long commentId, Long userId) {
        ReactionType reactionType = reaction(commentId, userId);
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
        ReactionType reactionType = reaction(commentId, userId);
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
    public ReactionType reaction(Long commentId, Long userId) {
        Optional<CommentReaction> byPostIdAndUserId = commentReactionRepository.findByCommentIdAndUserId(commentId, userId);
        if (byPostIdAndUserId.isPresent()) {
            return byPostIdAndUserId.get().getType();
        }
        return null;
    }

}