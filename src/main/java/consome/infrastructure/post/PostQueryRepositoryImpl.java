package consome.infrastructure.post;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import consome.domain.admin.QCategory;
import consome.domain.post.entity.QPost;
import consome.domain.post.entity.QPostStat;
import consome.domain.post.repository.PostQueryRepository;
import consome.domain.post.PostSummary;
import consome.domain.user.QUser;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;

import java.util.List;

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

        List<PostSummary> contents = queryFactory
                .select(Projections.constructor(
                        PostSummary.class,
                        post.id,
                        post.title,
                        post.categoryId,
                        category.name,
                        post.userId,
                        user.nickname,
                        postStat.viewCount,
                        postStat.likeCount,
                        postStat.dislikeCount,
                        postStat.commentCount,
                        post.createdAt,
                        post.updatedAt,
                        post.deleted
                ))
                .from(post)
                .leftJoin(user).on(post.userId.eq(user.id))
                .leftJoin(category).on(post.categoryId.eq(category.id))
                .leftJoin(postStat).on(post.id.eq(postStat.postId))
                .where(post.boardId.eq(boardId),
                        categoryEq(categoryId))
                .orderBy(post.createdAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long total = queryFactory
                .select(post.count())
                .from(post)
                .where(post.boardId.eq(boardId),
                        categoryEq(categoryId))
                .fetchOne();

        return PageableExecutionUtils.getPage(contents, pageable, () -> total == null ? 0L : total);
    }
}
