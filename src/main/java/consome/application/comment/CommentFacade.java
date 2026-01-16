package consome.application.comment;

import consome.domain.comment.Comment;
import consome.domain.comment.CommentQueryRepository;
import consome.domain.comment.CommentService;
import consome.domain.comment.CommentStat;
import consome.domain.point.PointHistoryType;
import consome.domain.point.PointService;
import consome.domain.post.PostService;
import consome.domain.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Service
@RequiredArgsConstructor
public class CommentFacade {

    private final CommentService commentService;
    private final PointService pointService;
    private final PostService postService;
    private final UserService userService;
    private final CommentQueryRepository commentQueryRepository;

    @Transactional
    public CommentResult comment(Long postId, Long userId, Long parentId, String content) {
        postService.getPost(postId);
        postService.increaseCommentCount(postId);
        String nickname = userService.findById(userId).getNickname();
        Comment comment = commentService.comment(postId, userId, parentId, content);
        CommentResult result = new CommentResult(
                comment.getId(),
                comment.getPostId(),
                comment.getUserId(),
                nickname,
                comment.getContent(),
                comment.getDepth(),
                comment.isDeleted(),
                comment.getCreatedAt(),
                comment.getUpdatedAt()
        );
        return result;
    }

    @Transactional
    public Comment edit(Long userId, Long commentId, String content) {
        return commentService.edit(userId, commentId, content);
    }

    @Transactional
    public Comment delete(Long userId, Long commentId) {
        return commentService.delete(userId, commentId);
    }

    @Transactional
    public CommentStat like(Long commentId, Long userId) {
        CommentStat stat = commentService.like(commentId, userId);
        pointService.earn(userId, PointHistoryType.COMMENT_LIKE);
        return stat;
    }

    @Transactional
    public CommentStat dislike(Long commentId, Long userId) {
        CommentStat stat = commentService.dislike(commentId, userId);
        pointService.penalize(userId, PointHistoryType.COMMENT_DISLIKE);
        return stat;
    }

    @Transactional(readOnly = true)
    public Page<CommentListResult> listByPost(Long postId, Pageable pageable) {
        return commentQueryRepository.findCommentsByPostId(postId, pageable);
    }
}
