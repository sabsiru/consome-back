package consome.application.user;


import consome.domain.board.BoardService;
import consome.domain.comment.Comment;
import consome.domain.comment.CommentReaction;
import consome.domain.comment.CommentService;
import consome.domain.point.PointHistoryType;
import consome.domain.point.PointService;
import consome.domain.post.Post;
import consome.domain.post.PostService;
import consome.domain.post.PostStat;
import consome.domain.post.ReactionType;
import consome.domain.user.User;
import consome.domain.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserFacade {

    private final UserService userService;
    private final PointService pointService;
    private final PostService postService;
    private final CommentService commentService;

    public Long register(UserCommand command) {
        User user = userService.register(command.getLoginId(), command.getNickname(), command.getPassword());
        pointService.initialize(user.getId());
        return user.getId();
    }

    @Transactional
    public Post post(long boardId, long categoryId, Long authorId, String title, String content) {
        pointService.earn(authorId, PointHistoryType.POST_WRITE);
        return postService.post(boardId, categoryId, authorId, title, content);
    }

    @Transactional
    public Post editPost(String title, String content, Long postId, Long userId) {
        return postService.edit(title, content, postId, userId);
    }

    @Transactional
    public Post deletePost(Long postId, Long userId) {
        pointService.penalize(userId, PointHistoryType.POST_DEL);
        return postService.delete(postId, userId);
    }

    @Transactional
    public PostStat likePost(Post post, Long userId) {
        pointService.earn(post.getAuthorId(), PointHistoryType.POST_LIKE);
        postService.like(post, userId);

        return postService.getPostStat(post.getId());
    }

    @Transactional
    public PostStat dislikePost(Post post, Long userId) {
        postService.dislike(post, userId);
        pointService.penalize(post.getAuthorId(), PointHistoryType.POST_DISLIKE);

        return postService.getPostStat(post.getId());
    }

    @Transactional
    public PostStat increaseViewCount(Long postId, String userIp, Long userId) {
        postService.increaseViewCount(postId, userIp, userId);

        return postService.getPostStat(postId);
    }

    @Transactional
    public Comment comment(Long postId, Long userId, Long parentId, String content) {
        postService.getPost(postId);
        postService.increaseCommentCount(postId);
        Comment comment = commentService.comment(postId, userId, parentId, content);
        pointService.earn(userId, PointHistoryType.COMMENT_WRITE);
        return comment;
    }

    @Transactional
    public Comment editComment(Long userId, Long commentId, String content) {
        Comment edit = commentService.edit(userId, commentId, content);

        return edit;
    }

    @Transactional
    public Comment deleteComment(Long userId, Long commentId) {
        pointService.penalize(commentId, PointHistoryType.COMMENT_DEL);
        return commentService.delete(userId, commentId);
    }

    @Transactional
    public long likeComment(Long commentId, Long userId) {
        commentService.like(commentId, userId);
        pointService.earn(userId, PointHistoryType.COMMENT_LIKE);

        return commentService.countReactions(commentId, ReactionType.LIKE);
    }

    @Transactional
    public long dislikeComment(Long commentId, Long userId) {
        commentService.dislike(commentId, userId);
        pointService.penalize(userId, PointHistoryType.COMMENT_DISLIKE);

        return commentService.countReactions(commentId, ReactionType.DISLIKE);
    }

    @Transactional
    public long cancelReactionComment(Long commentId, Long userId) {
        CommentReaction commentReaction = commentService.cancel(commentId, userId);
        if (commentReaction.getType() == ReactionType.LIKE) {
            pointService.penalize(userId, PointHistoryType.COMMENT_LIKE_CANCEL);
            return commentService.countReactions(commentId, ReactionType.LIKE);
        } else if (commentReaction.getType() == ReactionType.DISLIKE) {
            pointService.penalize(userId, PointHistoryType.COMMENT_DISLIKE_CANCEL);
            return commentService.countReactions(commentId, ReactionType.DISLIKE);
        }
        return 0;
    }
}
