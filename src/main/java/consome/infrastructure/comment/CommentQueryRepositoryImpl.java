package consome.infrastructure.comment;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.querydsl.core.Tuple;
import consome.application.comment.CommentListResult;
import consome.domain.comment.Comment;
import consome.domain.comment.CommentQueryRepository;
import consome.domain.comment.QComment;
import consome.domain.comment.QCommentStat;
import consome.domain.user.QUser;
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
    private final QComment parentComment = new QComment("parentComment");
    private final QUser user = QUser.user;
    private final QUser parentUser = new QUser("parentUser");
    private final QCommentStat commentStat = QCommentStat.commentStat;

    public Page<CommentListResult> findCommentsByPostId(Long postId, Pageable pageable) {
        List<Tuple> results = queryFactory
                .select(comment, user.nickname, parentUser.nickname, commentStat.likeCount, commentStat.dislikeCount)
                .from(comment)
                .join(user).on(user.id.eq(comment.userId))
                .leftJoin(parentComment).on(parentComment.id.eq(comment.parentId))
                .leftJoin(parentUser).on(parentUser.id.eq(parentComment.userId))
                .leftJoin(commentStat).on(commentStat.commentId.eq(comment.id))
                .where(comment.postId.eq(postId))
                .orderBy(comment.ref.asc(), comment.step.asc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long total = queryFactory
                .select(comment.count())
                .from(comment)
                .where(comment.postId.eq(postId))
                .fetchOne();

        return new PageImpl<>(
                results.stream()
                        .map(t -> {
                            Comment c = t.get(comment);
                            String nickname = t.get(user.nickname);
                            String parentNickname = t.get(parentUser.nickname);
                            Integer likeCount = t.get(commentStat.likeCount);
                            Integer dislikeCount = t.get(commentStat.dislikeCount);
                            return new CommentListResult(
                                    c.getId(),
                                    c.getPostId(),
                                    c.getUserId(),
                                    nickname,
                                    c.getParentId(),
                                    parentNickname,
                                    c.getContent(),
                                    c.getDepth(),
                                    likeCount != null ? likeCount : 0,
                                    dislikeCount != null ? dislikeCount : 0,
                                    c.isDeleted(),
                                    c.getCreatedAt(),
                                    c.getUpdatedAt(),
                                    false,
                                    false
                            );
                        })
                        .toList(),
                pageable,
                total != null ? total : 0
        );
    }

    public int allocateReplyStep(Long postId, int parentRef, int parentStep, int parentDepth) {
        Integer next = queryFactory
                .select(comment.step)
                .from(comment)
                .where(
                        comment.postId.eq(postId)
                                .and(comment.ref.eq(parentRef))
                                .and(comment.step.gt(parentStep))
                                .and(comment.depth.loe(parentDepth))
                )
                .orderBy(comment.step.asc())
                .fetchFirst();

        int newStep;
        if (next != null) {
            newStep = next;
        } else {
            Integer max = queryFactory
                    .select(comment.step.max())
                    .from(comment)
                    .where(
                            comment.postId.eq(postId)
                                    .and(comment.ref.eq(parentRef))
                    )
                    .fetchOne();
            newStep = (max != null ? max : parentStep) + 1;
        }

        queryFactory
                .update(comment)
                .set(comment.step, comment.step.add(1))
                .where(
                        comment.postId.eq(postId)
                                .and(comment.ref.eq(parentRef))
                                .and(comment.step.goe(newStep))
                )
                .execute();

        return newStep;
    }

    public int nextRef(Long postId) {
        Integer maxRef = queryFactory
                .select(comment.ref.max())
                .from(comment)
                .where(comment.postId.eq(postId))
                .fetchOne();

        return (maxRef == null ? 1 : maxRef + 1);
    }

    public Integer findBoundaryStep(Long postId, int ref, int parentStep, int parentDepth) {
        return queryFactory
                .select(comment.step)
                .from(comment)
                .where(
                        comment.postId.eq(postId)
                                .and(comment.ref.eq(ref))
                                .and(comment.step.gt(parentStep))
                                .and(comment.depth.loe(parentDepth))
                )
                .orderBy(comment.step.asc())
                .fetchFirst();
    }

    public int maxStepInRef(Long postId, int ref) {
        Integer max = queryFactory
                .select(comment.step.max())
                .from(comment)
                .where(
                        comment.postId.eq(postId)
                                .and(comment.ref.eq(ref))
                )
                .fetchOne();

        return (max == null ? 0 : max);
    }

    public void shiftStepsFrom(Long postId, int ref, int fromStep) {
         queryFactory
                .update(comment)
                .set(comment.step, comment.step.add(1))
                .where(
                        comment.postId.eq(postId)
                                .and(comment.ref.eq(ref))
                                .and(comment.step.goe(fromStep))
                )
                .execute();
    }
}
