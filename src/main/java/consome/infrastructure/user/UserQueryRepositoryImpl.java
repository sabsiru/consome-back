package consome.infrastructure.user;


import com.querydsl.jpa.impl.JPAQueryFactory;
import consome.domain.point.QPoint;
import consome.domain.user.QUser;
import consome.domain.user.QUserInfo;
import consome.domain.user.UserInfo;
import consome.domain.user.UserQueryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class UserQueryRepositoryImpl implements UserQueryRepository {
    private final JPAQueryFactory queryFactory;

    @Override
    public Page<UserInfo> findUsers(Pageable pageable) {
        QUser user = QUser.user;
        QPoint point = QPoint.point;

        // content 조회
        List<UserInfo> content = queryFactory
                .select(
                        new QUserInfo(
                                user.id,
                                user.loginId,
                                user.nickname,
                                user.role,
                                point.userPoint
                        )
                )
                .from(user)
                .leftJoin(point).on(point.userId.eq(user.id))   // FK 없으므로 명시적 join 조건 필요
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        // countQuery
        long total = queryFactory
                .select(user.count())
                .from(user)
                .fetchOne();

        return new PageImpl<>(content, pageable, total);
    }
}
