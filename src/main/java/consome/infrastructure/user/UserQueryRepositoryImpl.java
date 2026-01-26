package consome.infrastructure.user;


import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import consome.application.user.UserSearchCommand;
import consome.application.user.UserSearchResult;
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
import org.springframework.util.StringUtils;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class UserQueryRepositoryImpl implements UserQueryRepository {
    private final JPAQueryFactory queryFactory;
    QUser user = QUser.user;
    QPoint point = QPoint.point;

    @Override
    public Page<UserInfo> findUsers(Pageable pageable) {


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

    @Override
    public Page<UserSearchResult> search(UserSearchCommand command, Pageable pageable) {
        QUser user = QUser.user;

        BooleanBuilder builder = new BooleanBuilder();

        // 통합검색
        if (StringUtils.hasText(command.keyword())) {
            builder.and(
                    user.nickname.containsIgnoreCase(command.keyword())
                            .or(user.loginId.containsIgnoreCase(command.keyword()))
                            .or(user.id.stringValue().contains(command.keyword()))
            );
        }

        // 개별검색
        if (command.id() != null) {
            builder.and(user.id.eq(command.id()));
        }
        if (StringUtils.hasText(command.loginId())) {
            builder.and(user.loginId.containsIgnoreCase(command.loginId()));
        }
        if (StringUtils.hasText(command.nickname())) {
            builder.and(user.nickname.containsIgnoreCase(command.nickname()));
        }

        List<UserSearchResult> content = queryFactory
                .select(Projections.constructor(UserSearchResult.class,
                        user.id,
                        user.loginId,
                        user.nickname,
                        user.role,
                        point.userPoint
                ))
                .from(user)
                .leftJoin(point).on(point.userId.eq(user.id))
                .where(builder)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(user.id.desc())
                .fetch();

        Long total = queryFactory
                .select(user.count())
                .from(user)
                .where(builder)
                .fetchOne();

        return new PageImpl<>(content, pageable, total != null ? total : 0);
    }
}
