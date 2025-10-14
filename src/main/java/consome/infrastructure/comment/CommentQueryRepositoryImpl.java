package consome.infrastructure.comment;

import com.querydsl.jpa.impl.JPAQueryFactory;
import consome.domain.comment.Comment;
import consome.domain.comment.CommentQueryRepository;
import consome.domain.comment.QComment;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

@Repository
@RequiredArgsConstructor
public class CommentQueryRepositoryImpl implements CommentQueryRepository {

    private final JPAQueryFactory queryFactory;
    private final QComment comment = QComment.comment;

    public Page<Comment> findCommentsByPostId(Long postId, Pageable pageable) {
        List<Comment> comments = queryFactory
                .selectFrom(comment)
                .where(comment.postId.eq(postId))
                .orderBy(comment.ref.desc(), comment.step.asc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long total = queryFactory
                .select(comment.count())
                .from(comment)
                .where(comment.postId.eq(postId))
                .fetchOne();

        return new PageImpl<>(comments, pageable, total == null ? 0 : total);
    }


}
