package consome.infrastructure.comment;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.querydsl.core.Tuple;
import consome.application.comment.CommentResult;
import consome.domain.comment.Comment;
import consome.domain.comment.CommentQueryRepository;
import consome.domain.comment.QComment;
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
    private final QUser user = QUser.user;

    public Page<CommentResult> findCommentsByPostId(Long postId, Pageable pageable) {
        List<Tuple> results = queryFactory
                .select(comment, user.nickname)
                .from(comment)
                .join(user).on(user.id.eq(comment.userId))
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
                            return new CommentResult(
                                    c.getId(),
                                    c.getPostId(),
                                    c.getUserId(),
                                    nickname,
                                    c.getContent(),
                                    c.getDepth(),
                                    c.isDeleted(),
                                    c.getCreatedAt(),
                                    c.getUpdatedAt()
                            );
                        })
                        .toList(),
                pageable,
                total != null ? total : 0
        );
    }

    public int allocateReplyStep(Long postId, int parentRef, int parentStep, int parentDepth) {
        // 1) 부모 서브트리 다음으로 넘어가는 첫 step 찾기
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

        // 2) 삽입 위치 결정
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

        // 3) 삽입 위치부터 step 밀기 (반드시 >=)
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

    /**
     * 같은 ref 안에서 parentStep 뒤쪽을 보면서,
     * depth가 parentDepth 이하로 내려오는 "첫 번째 step"을 찾는다.
     * 이 step이 존재하면 그 위치가 '부모 서브트리 다음'이므로 삽입은 그 step에 한다.
     */
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
                                .and(comment.step.goe(fromStep)) // >=
                )
                .execute();
    }
}
