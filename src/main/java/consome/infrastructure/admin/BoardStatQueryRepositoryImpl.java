package consome.infrastructure.admin;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import consome.domain.admin.repository.BoardStatQueryRepository;
import consome.domain.post.entity.QPost;
import consome.domain.post.entity.QPostStat;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class BoardStatQueryRepositoryImpl implements BoardStatQueryRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<BoardStatRow> findBoardStats() {
        QPost post = QPost.post;
        QPostStat postStat = QPostStat.postStat;

        return queryFactory
                .select(Projections.constructor(
                        BoardStatRow.class,
                        post.boardId,
                        postStat.viewCount.avg().coalesce(0.0),
                        postStat.likeCount.avg().coalesce(0.0),
                        postStat.commentCount.avg().coalesce(0.0)
                ))
                .from(post)
                .leftJoin(postStat).on(post.id.eq(postStat.postId))
                .where(post.deleted.isFalse())
                .groupBy(post.boardId)
                .fetch();
    }
}
