package consome.infrastructure.comment;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import consome.domain.comment.Comment;
import consome.domain.comment.CommentQueryRepository;
import consome.domain.comment.QComment;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class CommentQueryRepositoryImpl implements CommentQueryRepository {

    private final JPAQueryFactory queryFactory;
    private final QComment comment = QComment.comment;

    @Override
    public List<Comment> findCommentsByPostId(Long postId, Long cursorId, int size, String sort) {
        BooleanExpression condition = comment.postId.eq(postId);
        if (cursorId != null) {
            condition = condition.and(sort.equals("desc")
                    ? comment.id.lt(cursorId)
                    : comment.id.gt(cursorId));
        }

        return queryFactory.selectFrom(comment)
                .where(condition)
                .orderBy(sort.equals("desc")
                        ? comment.id.desc()
                        : comment.id.asc())
                .limit(size)
                .fetch();
    }
}
