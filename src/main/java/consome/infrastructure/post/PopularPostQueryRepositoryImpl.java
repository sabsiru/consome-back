package consome.infrastructure.post;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import consome.application.navigation.PopularPostResult;
import consome.domain.admin.QBoard;
import consome.domain.post.entity.QPopularPost;
import consome.domain.post.entity.QPost;
import consome.domain.post.repository.PopularPostQueryRepository;
import consome.domain.user.QUser;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class PopularPostQueryRepositoryImpl implements PopularPostQueryRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<PopularPostResult> findPopularPosts(int limit) {
        QPopularPost popularPost = QPopularPost.popularPost;
        QPost post = QPost.post;
        QBoard board = QBoard.board;
        QUser user = QUser.user;

        return queryFactory
                .select(Projections.constructor(
                        PopularPostResult.class,
                        popularPost.postId,
                        popularPost.boardId,
                        board.name,
                        post.title,
                        user.nickname,
                        popularPost.viewCount,
                        popularPost.likeCount,
                        popularPost.commentCount,
                        popularPost.createdAt
                ))
                .from(popularPost)
                .leftJoin(post).on(popularPost.postId.eq(post.id))
                .leftJoin(board).on(popularPost.boardId.eq(board.id))
                .leftJoin(user).on(post.userId.eq(user.id))
                .where(post.deleted.isFalse())
                .orderBy(popularPost.createdAt.desc())
                .limit(limit)
                .fetch();
    }
}
