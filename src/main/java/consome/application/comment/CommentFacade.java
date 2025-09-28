package consome.application.comment;

import consome.domain.comment.Comment;
import consome.domain.comment.CommentReaction;
import consome.domain.comment.CommentService;
import consome.domain.point.PointHistoryType;
import consome.domain.point.PointService;
import consome.domain.post.PostService;
import consome.domain.post.ReactionType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CommentFacade {

    private final CommentService commentService;
    private final PointService pointService;
    private final PostService postService;

    @Transactional
    public Comment comment(Long postId, Long userId, Long parentId, String content) {
        postService.getPost(postId);
        postService.increaseCommentCount(postId);
        Comment comment = commentService.comment(postId, userId, parentId, content);
        pointService.earn(userId, PointHistoryType.COMMENT_WRITE);
        return comment;
    }

    @Transactional
    public Comment edit(Long userId, Long commentId, String content) {
        Comment edit = commentService.edit(userId, commentId, content);

        return edit;
    }

    @Transactional
    public Comment delete(Long userId, Long commentId) {
        pointService.penalize(commentId, PointHistoryType.COMMENT_DEL);
        return commentService.delete(userId, commentId);
    }

    @Transactional
    public long like(Long commentId, Long userId) {
        commentService.like(commentId, userId);
        pointService.earn(userId, PointHistoryType.COMMENT_LIKE);

        return commentService.countReactions(commentId, ReactionType.LIKE);
    }

    @Transactional
    public long dislike(Long commentId, Long userId) {
        commentService.dislike(commentId, userId);
        pointService.penalize(userId, PointHistoryType.COMMENT_DISLIKE);

        return commentService.countReactions(commentId, ReactionType.DISLIKE);
    }
//    @Transactional
//    public CommentPage listByPost(Long postId, Long cursorId, int size, String sort) {
//        postService.getPost(postId);
//        return null;
//    }
}