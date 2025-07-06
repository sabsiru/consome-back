package consome.infrastructure.post;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import consome.domain.post.PostQueryRepository;
import consome.domain.post.PostSummary;
import consome.domain.post.QPost;
import consome.domain.post.QPostStat;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class PostQueryRepositoryImpl  implements PostQueryRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<PostSummary> findPostWithStatsByBoardId(Long boardId, Pageable pageable) {
        QPost post = QPost.post;
        QPostStat postStat = QPostStat.postStat;

        List<PostSummary> contents = queryFactory
                .select(Projections.constructor(
                        PostSummary.class,
                        post.id,
                        post.title,
                        postStat.likeCount,
                        postStat.viewCount,
                        postStat.dislikeCount,
                        postStat.commentCount
                ))
                .from(post)
                .leftJoin(postStat).on(post.id.eq(postStat.postId))
                .where(post.boardId.eq(boardId))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long total = queryFactory
                .select(post.count())
                .from(post)
                .where(post.boardId.eq(boardId))
                .fetchOne();

        return PageableExecutionUtils.getPage(contents, pageable, () -> total == null ? 0L : total);
    }
}
