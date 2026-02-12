package consome.infrastructure.user;


import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQueryFactory;
import consome.application.user.UserCommentResult;
import consome.application.user.UserPostResult;
import consome.application.user.UserSearchCommand;
import consome.application.user.UserSearchResult;
import consome.domain.admin.QBoard;
import consome.domain.comment.QComment;
import consome.domain.comment.QCommentStat;
import consome.domain.level.LevelInfo;
import consome.domain.point.QPoint;
import consome.domain.post.entity.QPost;
import consome.domain.post.entity.QPostStat;
import consome.domain.user.QUser;
import consome.domain.user.UserInfo;
import consome.domain.user.repository.UserQueryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class UserQueryRepositoryImpl implements UserQueryRepository {
    private final JPAQueryFactory queryFactory;
    QUser user = QUser.user;
    QPoint point = QPoint.point;
    QPost post = QPost.post;
    QPostStat postStat = QPostStat.postStat;
    QComment comment = QComment.comment;
    QCommentStat commentStat = QCommentStat.commentStat;
    QBoard board = QBoard.board;

    @Override
    public Page<UserInfo> findUsers(Pageable pageable) {
        // content 조회
        List<Tuple> tuples = queryFactory
                .select(
                        user.id,
                        user.loginId,
                        user.nickname,
                        user.role,
                        point.userPoint
                )
                .from(user)
                .leftJoin(point).on(point.userId.eq(user.id))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        List<UserInfo> content = tuples.stream()
                .map(t -> {
                    Integer userPoint = t.get(point.userPoint);
                    int pointValue = userPoint != null ? userPoint : 0;
                    return new UserInfo(
                            t.get(user.id),
                            t.get(user.loginId),
                            t.get(user.nickname),
                            t.get(user.role),
                            pointValue,
                            LevelInfo.calculateLevel(pointValue).getLevel()
                    );
                })
                .toList();

        // countQuery
        long total = queryFactory
                .select(user.count())
                .from(user)
                .fetchOne();

        return new PageImpl<>(content, pageable, total);
    }

    @Override
    public Page<UserSearchResult> search(UserSearchCommand command, Pageable pageable) {
        QUser user = QUser.user;

        BooleanBuilder builder = new BooleanBuilder();

        // 통합검색
        if (StringUtils.hasText(command.keyword())) {
            builder.and(
                    user.nickname.containsIgnoreCase(command.keyword())
                            .or(user.loginId.containsIgnoreCase(command.keyword()))
                            .or(user.id.stringValue().contains(command.keyword()))
            );
        }

        // 개별검색
        if (command.id() != null) {
            builder.and(user.id.eq(command.id()));
        }
        if (StringUtils.hasText(command.loginId())) {
            builder.and(user.loginId.containsIgnoreCase(command.loginId()));
        }
        if (StringUtils.hasText(command.nickname())) {
            builder.and(user.nickname.containsIgnoreCase(command.nickname()));
        }

        List<Tuple> tuples = queryFactory
                .select(
                        user.id,
                        user.loginId,
                        user.nickname,
                        user.role,
                        point.userPoint
                )
                .from(user)
                .leftJoin(point).on(point.userId.eq(user.id))
                .where(builder)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(user.id.desc())
                .fetch();

        List<UserSearchResult> content = tuples.stream()
                .map(t -> {
                    Integer userPoint = t.get(point.userPoint);
                    int pointValue = userPoint != null ? userPoint : 0;
                    return new UserSearchResult(
                            t.get(user.id),
                            t.get(user.loginId),
                            t.get(user.nickname),
                            t.get(user.role),
                            pointValue,
                            LevelInfo.calculateLevel(pointValue).getLevel()
                    );
                })
                .toList();

        Long total = queryFactory
                .select(user.count())
                .from(user)
                .where(builder)
                .fetchOne();

        return new PageImpl<>(content, pageable, total != null ? total : 0);
    }

    @Override
    public Page<UserPostResult> findPostsByUserId(Long userId, Pageable pageable) {
        List<Tuple> tuples = queryFactory
                .select(
                        post.id,
                        post.boardId,
                        board.name,
                        post.title,
                        postStat.viewCount,
                        postStat.likeCount,
                        postStat.commentCount,
                        post.createdAt
                )
                .from(post)
                .leftJoin(board).on(post.boardId.eq(board.id))
                .leftJoin(postStat).on(post.id.eq(postStat.postId))
                .where(post.userId.eq(userId), post.deleted.isFalse())
                .orderBy(post.createdAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        List<UserPostResult> content = tuples.stream()
                .map(t -> new UserPostResult(
                        t.get(post.id),
                        t.get(post.boardId),
                        t.get(board.name),
                        t.get(post.title),
                        t.get(postStat.viewCount) != null ? t.get(postStat.viewCount) : 0,
                        t.get(postStat.likeCount) != null ? t.get(postStat.likeCount) : 0,
                        t.get(postStat.commentCount) != null ? t.get(postStat.commentCount) : 0,
                        t.get(post.createdAt)
                ))
                .toList();

        Long total = queryFactory
                .select(post.count())
                .from(post)
                .where(post.userId.eq(userId), post.deleted.isFalse())
                .fetchOne();

        return new PageImpl<>(content, pageable, total != null ? total : 0);
    }

    @Override
    public Page<UserCommentResult> findCommentsByUserId(Long userId, Pageable pageable) {
        List<Tuple> tuples = queryFactory
                .select(
                        comment.id,
                        comment.postId,
                        post.title,
                        post.boardId,
                        board.name,
                        comment.content,
                        commentStat.likeCount,
                        comment.createdAt
                )
                .from(comment)
                .leftJoin(post).on(comment.postId.eq(post.id))
                .leftJoin(board).on(post.boardId.eq(board.id))
                .leftJoin(commentStat).on(comment.id.eq(commentStat.commentId))
                .where(comment.userId.eq(userId), comment.deleted.isFalse())
                .orderBy(comment.createdAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        List<UserCommentResult> content = tuples.stream()
                .map(t -> new UserCommentResult(
                        t.get(comment.id),
                        t.get(comment.postId),
                        t.get(post.title),
                        t.get(post.boardId),
                        t.get(board.name),
                        t.get(comment.content),
                        t.get(commentStat.likeCount) != null ? t.get(commentStat.likeCount) : 0,
                        t.get(comment.createdAt)
                ))
                .toList();

        Long total = queryFactory
                .select(comment.count())
                .from(comment)
                .where(comment.userId.eq(userId), comment.deleted.isFalse())
                .fetchOne();

        return new PageImpl<>(content, pageable, total != null ? total : 0);
    }

    @Override
    public int countPostsByUserId(Long userId) {
        Long count = queryFactory
                .select(post.count())
                .from(post)
                .where(post.userId.eq(userId), post.deleted.isFalse())
                .fetchOne();
        return count != null ? count.intValue() : 0;
    }

    @Override
    public int countCommentsByUserId(Long userId) {
        Long count = queryFactory
                .select(comment.count())
                .from(comment)
                .where(comment.userId.eq(userId), comment.deleted.isFalse())
                .fetchOne();
        return count != null ? count.intValue() : 0;
    }
}
