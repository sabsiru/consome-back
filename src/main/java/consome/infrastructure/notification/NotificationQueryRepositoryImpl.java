package consome.infrastructure.notification;

import consome.application.notification.NotificationResult;
import consome.domain.notification.repository.NotificationQueryRepository;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

import static consome.domain.notification.QNotification.notification;
import static consome.domain.user.QUser.user;

@Repository
@RequiredArgsConstructor
public class NotificationQueryRepositoryImpl implements NotificationQueryRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<NotificationResult> findByUserId(Long userId, Pageable pageable) {
        List<NotificationResult> content = queryFactory
                .select(Projections.constructor(NotificationResult.class,
                        notification.id,
                        notification.userId,
                        notification.type,
                        notification.actorId,
                        user.nickname,
                        notification.targetId,
                        notification.relatedId,
                        notification.referenceId,
                        notification.message,
                        notification.isRead,
                        notification.createdAt,
                        notification.readAt
                ))
                .from(notification)
                .leftJoin(user).on(user.id.eq(notification.actorId))
                .where(notification.userId.eq(userId))
                .orderBy(notification.createdAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long total = queryFactory
                .select(notification.count())
                .from(notification)
                .where(notification.userId.eq(userId))
                .fetchOne();

        return new PageImpl<>(content, pageable, total != null ? total : 0);
    }
}
