package consome.application.user;


import consome.domain.comment.Comment;
import consome.domain.comment.CommentReaction;
import consome.domain.comment.CommentService;
import consome.domain.point.PointHistoryType;
import consome.domain.point.PointService;
import consome.domain.post.Post;
import consome.domain.post.PostService;
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
    public void deletePost(Long postId, Long userId) {
        postService.delete(postId, userId);
        pointService.penalize(userId, PointHistoryType.POST_DEL);
    }

    @Transactional
    public void likePost(Post post, Long userId) {
        postService.like(post, userId);
        pointService.earn(post.getAuthorId(), PointHistoryType.POST_LIKE);
    }

    @Transactional
    public void dislikePost(Post post, Long userId) {
        postService.dislike(post, userId);
        pointService.penalize(post.getAuthorId(), PointHistoryType.POST_DISLIKE);
    }

    @Transactional
    public void increaseViewCount(Long postId, Long userId, String userIp) {
        postService.increaseViewCount(postId, userId, userIp);
    }

    @Transactional
    public Comment comment(Long postId, Long userId, Long parentId, String content) {
        postService.getPost(postId);
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
    public void deleteComment(Long userId, Long commentId) {
        commentService.delete(userId, commentId);
        pointService.penalize(commentId, PointHistoryType.COMMENT_DEL);
    }

    @Transactional
    public void likeComment(Long commentId, Long userId) {
        commentService.like(commentId, userId);
        pointService.earn(userId, PointHistoryType.COMMENT_LIKE);
    }

    @Transactional
    public void dislikeComment(Long commentId, Long userId) {
        commentService.dislike(commentId, userId);
        pointService.penalize(userId, PointHistoryType.COMMENT_DISLIKE);
    }

    @Transactional
    public void cancelReactionComment(Long commentId, Long userId) {
        CommentReaction commentReaction = commentService.cancel(commentId, userId);
        if (commentReaction.getType() == ReactionType.LIKE) {
            pointService.penalize(userId, PointHistoryType.COMMENT_LIKE_CANCEL);
        } else if (commentReaction.getType() == ReactionType.DISLIKE) {
            pointService.penalize(userId, PointHistoryType.COMMENT_DISLIKE_CANCEL);
        }
    }
}
