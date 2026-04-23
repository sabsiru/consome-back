package consome.application.comment;

import consome.domain.admin.Board;
import consome.domain.admin.BoardService;
import consome.domain.admin.Section;
import consome.domain.admin.SectionService;
import consome.application.notification.NotificationFacade;
import consome.domain.comment.*;
import consome.domain.comment.repository.CommentQueryRepository;
import consome.domain.comment.repository.CommentReactionRepository;
import consome.domain.common.exception.BusinessException;
import consome.domain.notification.NotificationType;
import consome.domain.point.PointHistoryType;
import consome.domain.point.PointService;
import consome.domain.post.PopularPostService;
import consome.domain.post.PostService;
import consome.domain.post.ReactionType;
import consome.domain.post.entity.Post;
import consome.domain.statistics.ActivityStatService;
import consome.domain.statistics.ActivityType;
import consome.domain.user.UserService;
import consome.infrastructure.security.HtmlSanitizer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentFacade {

    private final CommentService commentService;
    private final PointService pointService;
    private final PostService postService;
    private final PopularPostService popularPostService;
    private final UserService userService;
    private final CommentQueryRepository commentQueryRepository;
    private final CommentReactionRepository commentReactionRepository;
    private final BoardService boardService;
    private final SectionService sectionService;
    private final NotificationFacade notificationFacade;
    private final HtmlSanitizer htmlSanitizer;
    private final ActivityStatService activityStatService;

    @Transactional
    public CommentResult comment(Long postId, Long userId, Long parentId, String content) {
        content = htmlSanitizer.sanitizeComment(content);
        Post post = postService.getPost(postId);
        validateCommentPermission(post.getBoardId(), userId);
        postService.increaseCommentCount(postId);
        popularPostService.updateScore(postId);
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

        // 알림 트리거
        sendCommentNotification(post, comment, userId, nickname, parentId);

        activityStatService.recordActivity(ActivityType.COMMENT);
        return result;
    }

    @Transactional
    public Comment edit(Long userId, Long commentId, String content) {
        content = htmlSanitizer.sanitizeComment(content);
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
        activityStatService.recordActivity(ActivityType.COMMENT_LIKE);
        return stat;
    }

    @Transactional
    public CommentStat dislike(Long commentId, Long userId) {
        CommentStat stat = commentService.dislike(commentId, userId);
        pointService.penalize(userId, PointHistoryType.COMMENT_DISLIKE);
        activityStatService.recordActivity(ActivityType.COMMENT_DISLIKE);
        return stat;
    }

    @Transactional
    public boolean hasLiked(Long commentId, Long userId) {
        return commentReactionRepository.findByCommentIdAndUserIdAndTypeAndDeletedFalse(commentId, userId, ReactionType.LIKE).isPresent();
    }

    @Transactional
    public boolean hasDisliked(Long commentId, Long userId) {
        return commentReactionRepository.findByCommentIdAndUserIdAndTypeAndDeletedFalse(commentId, userId, ReactionType.DISLIKE).isPresent();
    }

    private static final int POPULAR_COMMENT_MIN_TOTAL = 10;
    private static final int POPULAR_COMMENT_LIMIT = 3;

    @Transactional(readOnly = true)
    public CommentPageResult listByPost(Long postId, Long userId, Pageable pageable) {
        Page<CommentListResult> comments =
                commentQueryRepository.findCommentsByPostId(postId, pageable);

        if (userId != null) {
            comments = comments.map(c -> new CommentListResult(
                    c.commentId(), c.postId(), c.userId(), c.userNickname(), c.userLevel(),
                    c.parentId(), c.parentUserNickname(),
                    c.content(), c.depth(),
                    c.likeCount(), c.dislikeCount(),
                    c.isDeleted(), c.createdAt(), c.updatedAt(),
                    hasLiked(c.commentId(), userId),
                    hasDisliked(c.commentId(), userId)
            ));
        }

        List<CommentListResult> popularComments = List.of();
        boolean isFirstPage = pageable.getPageNumber() == 0;
        if (isFirstPage && comments.getTotalElements() >= POPULAR_COMMENT_MIN_TOTAL) {
            popularComments = commentQueryRepository.findPopularComments(postId, POPULAR_COMMENT_LIMIT);
            if (userId != null) {
                popularComments = popularComments.stream()
                        .map(c -> new CommentListResult(
                                c.commentId(), c.postId(), c.userId(), c.userNickname(), c.userLevel(),
                                c.parentId(), c.parentUserNickname(),
                                c.content(), c.depth(),
                                c.likeCount(), c.dislikeCount(),
                                c.isDeleted(), c.createdAt(), c.updatedAt(),
                                hasLiked(c.commentId(), userId),
                                hasDisliked(c.commentId(), userId)
                        ))
                        .toList();
            }
        }

        return new CommentPageResult(popularComments, comments);
    }

    private void sendCommentNotification(Post post, Comment comment, Long actorId, String actorNickname, Long parentId) {
        Long boardId = post.getBoardId();

        if (parentId != null) {
            // 대댓글 → 부모 댓글 작성자에게 REPLY 알림
            Comment parent = commentService.findById(parentId);
            notificationFacade.notify(parent.getUserId(), NotificationType.REPLY, actorId,
                    post.getId(), comment.getId(), boardId,
                    actorNickname + "님이 회원님의 댓글에 답글을 남겼습니다.");

            // 부모 댓글 작성자 == 게시글 작성자면 COMMENT 알림 중복 방지
            if (parent.getUserId().equals(post.getUserId())) {
                return;
            }
        }

        // 게시글 작성자에게 COMMENT 알림
        notificationFacade.notify(post.getUserId(), NotificationType.COMMENT, actorId,
                post.getId(), comment.getId(), boardId,
                actorNickname + "님이 회원님의 게시글에 댓글을 남겼습니다.");
    }

    private void validateCommentPermission(Long boardId, Long userId) {
        Board board = boardService.findById(boardId);

        // commentEnabled=true면 누구나 댓글 가능
        if (board.isCommentEnabled()) {
            return;
        }

        // commentEnabled=false면 ADMIN만 가능
        boolean isAdmin = userService.findById(userId).getRole() == consome.domain.user.Role.ADMIN;
        if (!isAdmin) {
            throw new BusinessException("COMMENT_DISABLED", "댓글이 제한된 게시판입니다.");
        }
    }
}
