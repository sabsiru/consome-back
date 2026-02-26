package consome.infrastructure.post;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import consome.domain.comment.QComment;
import consome.domain.admin.QBoard;
import consome.domain.admin.QCategory;
import consome.domain.post.BoardPopularityRow;
import consome.domain.post.PopularPostRow;
import consome.domain.post.PopularityType;
import consome.domain.post.PostPreviewRow;
import consome.domain.post.PostSummary;
import consome.domain.post.entity.QPost;
import consome.domain.post.entity.QPostStat;
import consome.domain.post.repository.PostQueryRepository;
import consome.domain.level.LevelInfo;
import consome.domain.point.QPoint;
import consome.domain.user.QUser;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class PostQueryRepositoryImpl implements PostQueryRepository {

    private final JPAQueryFactory queryFactory;

    private BooleanExpression categoryEq(Long categoryId) {
        return categoryId == null ? null : QPost.post.categoryId.eq(categoryId);
    }

    @Override
    public Page<PostSummary> findPostWithStatsByBoardId(Long boardId, Pageable pageable, Long categoryId) {
        QPost post = QPost.post;
        QPostStat postStat = QPostStat.postStat;
        QUser user = QUser.user;
        QCategory category = QCategory.category;
        QPoint point = QPoint.point;

        List<com.querydsl.core.Tuple> tuples = queryFactory
                .select(
                        post.id,
                        post.title,
                        post.categoryId,
                        category.name,
                        post.userId,
                        user.nickname,
                        point.userPoint,
                        user.role,
                        postStat.viewCount,
                        postStat.likeCount,
                        postStat.dislikeCount,
                        postStat.commentCount,
                        post.createdAt,
                        post.updatedAt,
                        post.deleted,
                        post.isPinned,
                        post.pinnedOrder
                )
                .from(post)
                .leftJoin(user).on(post.userId.eq(user.id))
                .leftJoin(category).on(post.categoryId.eq(category.id))
                .leftJoin(postStat).on(post.id.eq(postStat.postId))
                .leftJoin(point).on(post.userId.eq(point.userId))
                .where(post.boardId.eq(boardId),
                        categoryEq(categoryId))
                .orderBy(
                        post.isPinned.desc(),
                        new CaseBuilder()
                                .when(post.isPinned.isTrue()).then(post.pinnedOrder)
                                .otherwise((Integer) null)
                                .asc().nullsLast(),
                        post.createdAt.desc()
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        List<PostSummary> contents = tuples.stream()
                .map(t -> new PostSummary(
                        t.get(post.id),
                        t.get(post.title),
                        t.get(post.categoryId),
                        t.get(category.name),
                        t.get(post.userId),
                        t.get(user.nickname),
                        LevelInfo.calculateLevel(t.get(point.userPoint) != null ? t.get(point.userPoint) : 0).getLevel(),
                        t.get(user.role),
                        t.get(postStat.viewCount) != null ? t.get(postStat.viewCount) : 0,
                        t.get(postStat.likeCount) != null ? t.get(postStat.likeCount) : 0,
                        t.get(postStat.dislikeCount) != null ? t.get(postStat.dislikeCount) : 0,
                        t.get(postStat.commentCount) != null ? t.get(postStat.commentCount) : 0,
                        t.get(post.createdAt),
                        t.get(post.updatedAt),
                        t.get(post.deleted),
                        t.get(post.isPinned),
                        t.get(post.pinnedOrder)
                ))
                .toList();

        Long total = queryFactory
                .select(post.count())
                .from(post)
                .where(post.boardId.eq(boardId),
                        categoryEq(categoryId))
                .fetchOne();

        return PageableExecutionUtils.getPage(contents, pageable, () -> total == null ? 0L : total);
    }

    @Override
    public List<BoardPopularityRow> findPopularBoards(LocalDateTime since, PopularityType sortBy, int limit) {
        QPost post = QPost.post;
        QPostStat postStat = QPostStat.postStat;
        QBoard board = QBoard.board;

        NumberExpression<Double> scoreExpression = buildScoreExpression(sortBy, postStat, post);

        return queryFactory
                .select(Projections.constructor(
                        BoardPopularityRow.class,
                        post.boardId,
                        board.name,
                        scoreExpression
                ))
                .from(post)
                .leftJoin(postStat).on(post.id.eq(postStat.postId))
                .leftJoin(board).on(post.boardId.eq(board.id))
                .where(post.createdAt.goe(since))
                .groupBy(post.boardId, board.name)
                .orderBy(scoreExpression.desc())
                .limit(limit)
                .fetch();
    }

    @Override
    public List<PostPreviewRow> findLatestPostsByBoardIds(List<Long> boardIds, int previewLimit) {
        if (boardIds.isEmpty()) {
            return List.of();
        }

        QPost post = QPost.post;
        QPostStat postStat = QPostStat.postStat;
        QUser user = QUser.user;
        QPoint point = QPoint.point;

        // 각 게시판별 최신 게시글 조회 (ROW_NUMBER 대신 Java에서 그룹핑)
        List<com.querydsl.core.Tuple> tuples = queryFactory
                .select(
                        post.id,
                        post.boardId,
                        post.title,
                        user.nickname,
                        point.userPoint,
                        postStat.viewCount.coalesce(0),
                        postStat.likeCount.coalesce(0),
                        postStat.commentCount.coalesce(0),
                        post.createdAt
                )
                .from(post)
                .leftJoin(postStat).on(post.id.eq(postStat.postId))
                .leftJoin(user).on(post.userId.eq(user.id))
                .leftJoin(point).on(post.userId.eq(point.userId))
                .where(post.boardId.in(boardIds))
                .orderBy(post.boardId.asc(), post.createdAt.desc())
                .fetch();

        List<PostPreviewRow> allPosts = tuples.stream()
                .map(t -> new PostPreviewRow(
                        t.get(post.id),
                        t.get(post.boardId),
                        t.get(post.title),
                        t.get(user.nickname),
                        LevelInfo.calculateLevel(t.get(point.userPoint) != null ? t.get(point.userPoint) : 0).getLevel(),
                        t.get(postStat.viewCount.coalesce(0)),
                        t.get(postStat.likeCount.coalesce(0)),
                        t.get(postStat.commentCount.coalesce(0)),
                        t.get(post.createdAt)
                ))
                .toList();

        // 게시판별로 그룹핑하여 previewLimit만큼 제한
        Map<Long, List<PostPreviewRow>> grouped = allPosts.stream()
                .collect(Collectors.groupingBy(PostPreviewRow::boardId));

        return boardIds.stream()
                .flatMap(boardId -> grouped.getOrDefault(boardId, List.of()).stream()
                        .limit(previewLimit))
                .toList();
    }

    @Override
    public List<PopularPostRow> findPopularPosts(LocalDateTime since, int minViews) {
        QPost post = QPost.post;
        QPostStat postStat = QPostStat.postStat;
        QBoard board = QBoard.board;
        QUser user = QUser.user;
        QPoint point = QPoint.point;

        List<com.querydsl.core.Tuple> tuples = queryFactory
                .select(
                        post.id,
                        post.boardId,
                        board.name,
                        post.title,
                        user.nickname,
                        point.userPoint,
                        postStat.viewCount.coalesce(0),
                        postStat.likeCount.coalesce(0),
                        postStat.commentCount.coalesce(0),
                        post.createdAt
                )
                .from(post)
                .leftJoin(postStat).on(post.id.eq(postStat.postId))
                .leftJoin(board).on(post.boardId.eq(board.id))
                .leftJoin(user).on(post.userId.eq(user.id))
                .leftJoin(point).on(post.userId.eq(point.userId))
                .where(
                        post.createdAt.goe(since),
                        postStat.viewCount.goe(minViews)
                )
                .orderBy(post.createdAt.desc())
                .fetch();

        return tuples.stream()
                .map(t -> new PopularPostRow(
                        t.get(post.id),
                        t.get(post.boardId),
                        t.get(board.name),
                        t.get(post.title),
                        t.get(user.nickname),
                        LevelInfo.calculateLevel(t.get(point.userPoint) != null ? t.get(point.userPoint) : 0).getLevel(),
                        t.get(postStat.viewCount.coalesce(0)),
                        t.get(postStat.likeCount.coalesce(0)),
                        t.get(postStat.commentCount.coalesce(0)),
                        t.get(post.createdAt)
                ))
                .toList();
    }

    private NumberExpression<Double> buildScoreExpression(PopularityType sortBy, QPostStat postStat, QPost post) {
        return switch (sortBy) {
            case VIEW_COUNT -> postStat.viewCount.sum().doubleValue();
            case LIKE_COUNT -> postStat.likeCount.sum().doubleValue();
            case COMMENT_COUNT -> postStat.commentCount.sum().doubleValue();
            case POST_COUNT -> post.id.count().doubleValue();
            case COMPOSITE -> postStat.viewCount.sum().doubleValue().multiply(0.1)
                    .add(postStat.commentCount.sum().doubleValue().multiply(0.8))
                    .add(postStat.likeCount.sum().doubleValue().multiply(0.5))
                    .add(post.id.count().doubleValue().multiply(0.2));
        };
    }

    @Override
    public Page<PostSummary> searchPosts(Long boardId, String keyword, String searchType, Pageable pageable) {
        QPost post = QPost.post;
        QPostStat postStat = QPostStat.postStat;
        QUser user = QUser.user;
        QCategory category = QCategory.category;
        QComment comment = QComment.comment;
        QPoint point = QPoint.point;

        BooleanExpression searchCondition = buildSearchCondition(post, user, comment, keyword, searchType);

        List<com.querydsl.core.Tuple> tuples = queryFactory
                .select(
                        post.id,
                        post.title,
                        post.categoryId,
                        category.name,
                        post.userId,
                        user.nickname,
                        point.userPoint,
                        user.role,
                        postStat.viewCount,
                        postStat.likeCount,
                        postStat.dislikeCount,
                        postStat.commentCount,
                        post.createdAt,
                        post.updatedAt,
                        post.deleted,
                        post.isPinned,
                        post.pinnedOrder
                )
                .from(post)
                .leftJoin(user).on(post.userId.eq(user.id))
                .leftJoin(category).on(post.categoryId.eq(category.id))
                .leftJoin(postStat).on(post.id.eq(postStat.postId))
                .leftJoin(point).on(post.userId.eq(point.userId))
                .where(
                        post.boardId.eq(boardId),
                        searchCondition
                )
                .orderBy(
                        post.isPinned.desc(),
                        new CaseBuilder()
                                .when(post.isPinned.isTrue()).then(post.pinnedOrder)
                                .otherwise((Integer) null)
                                .asc().nullsLast(),
                        post.createdAt.desc()
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        List<PostSummary> contents = tuples.stream()
                .map(t -> new PostSummary(
                        t.get(post.id),
                        t.get(post.title),
                        t.get(post.categoryId),
                        t.get(category.name),
                        t.get(post.userId),
                        t.get(user.nickname),
                        LevelInfo.calculateLevel(t.get(point.userPoint) != null ? t.get(point.userPoint) : 0).getLevel(),
                        t.get(user.role),
                        t.get(postStat.viewCount) != null ? t.get(postStat.viewCount) : 0,
                        t.get(postStat.likeCount) != null ? t.get(postStat.likeCount) : 0,
                        t.get(postStat.dislikeCount) != null ? t.get(postStat.dislikeCount) : 0,
                        t.get(postStat.commentCount) != null ? t.get(postStat.commentCount) : 0,
                        t.get(post.createdAt),
                        t.get(post.updatedAt),
                        t.get(post.deleted),
                        t.get(post.isPinned),
                        t.get(post.pinnedOrder)
                ))
                .toList();

        Long total = queryFactory
                .select(post.count())
                .from(post)
                .leftJoin(user).on(post.userId.eq(user.id))
                .where(
                        post.boardId.eq(boardId),
                        searchCondition
                )
                .fetchOne();

        return PageableExecutionUtils.getPage(contents, pageable, () -> total == null ? 0L : total);
    }

    private BooleanExpression buildSearchCondition(QPost post, QUser user, QComment comment, String keyword, String searchType) {
        if (keyword == null || keyword.isBlank()) {
            return null;
        }

        return switch (searchType.toLowerCase()) {
            case "title" -> post.title.containsIgnoreCase(keyword);
            case "content" -> post.content.containsIgnoreCase(keyword);
            case "nickname" -> user.nickname.containsIgnoreCase(keyword);
            case "comment" -> post.id.in(
                    JPAExpressions.select(comment.postId)
                            .from(comment)
                            .where(
                                    comment.content.containsIgnoreCase(keyword),
                                    comment.deleted.isFalse()
                            )
            );
            default -> post.title.containsIgnoreCase(keyword)
                    .or(post.content.containsIgnoreCase(keyword))
                    .or(user.nickname.containsIgnoreCase(keyword))
                    .or(post.id.in(
                            JPAExpressions.select(comment.postId)
                                    .from(comment)
                                    .where(
                                            comment.content.containsIgnoreCase(keyword),
                                            comment.deleted.isFalse()
                                    )
                    ));
        };
    }

    @Override
    public Optional<Integer> findMaxPinnedOrderByBoardId(Long boardId) {
        QPost post = QPost.post;

        Integer max = queryFactory
                .select(post.pinnedOrder.max())
                .from(post)
                .where(
                        post.boardId.eq(boardId),
                        post.isPinned.isTrue()
                )
                .fetchOne();

        return Optional.ofNullable(max);
    }
}
