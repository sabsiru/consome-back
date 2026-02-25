package consome.infrastructure.report;

import com.querydsl.core.Tuple;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import consome.domain.report.entity.Report;
import consome.domain.report.entity.ReportStatus;
import consome.domain.report.entity.ReportTargetType;
import consome.domain.report.repository.ReportQueryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

import static consome.domain.report.entity.QReport.report;

@Repository
@RequiredArgsConstructor
public class ReportQueryRepositoryImpl implements ReportQueryRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<Report> findAllWithFilters(ReportStatus status, ReportTargetType targetType, Long targetUserId, Pageable pageable) {
        List<Report> content = queryFactory
                .selectFrom(report)
                .where(
                        statusEq(status),
                        targetTypeEq(targetType),
                        targetUserIdEq(targetUserId)
                )
                .orderBy(report.createdAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long total = queryFactory
                .select(report.count())
                .from(report)
                .where(
                        statusEq(status),
                        targetTypeEq(targetType),
                        targetUserIdEq(targetUserId)
                )
                .fetchOne();

        return new PageImpl<>(content, pageable, total != null ? total : 0);
    }

    private BooleanExpression statusEq(ReportStatus status) {
        return status != null ? report.status.eq(status) : null;
    }

    private BooleanExpression targetTypeEq(ReportTargetType targetType) {
        return targetType != null ? report.targetType.eq(targetType) : null;
    }

    private BooleanExpression targetUserIdEq(Long targetUserId) {
        return targetUserId != null ? report.targetUserId.eq(targetUserId) : null;
    }

    @Override
    public Page<GroupedReportProjection> findAllGrouped(ReportStatus status, Pageable pageable) {
        List<Tuple> results = queryFactory
                .select(
                        report.targetType,
                        report.targetId,
                        report.id.min(),
                        report.count()
                )
                .from(report)
                .where(statusEq(status))
                .groupBy(report.targetType, report.targetId)
                .orderBy(report.id.min().desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        List<GroupedReportProjection> content = results.stream()
                .map(tuple -> new GroupedReportProjection(
                        tuple.get(report.targetType),
                        tuple.get(report.targetId),
                        tuple.get(report.id.min()),
                        tuple.get(report.count()).intValue(),
                        determineRepresentativeStatus(tuple.get(report.targetType), tuple.get(report.targetId))
                ))
                .toList();

        Long total = (long) queryFactory
                .select(report.targetType)
                .from(report)
                .where(statusEq(status))
                .groupBy(report.targetType, report.targetId)
                .fetch()
                .size();

        return new PageImpl<>(content, pageable, total);
    }

    private ReportStatus determineRepresentativeStatus(ReportTargetType targetType, Long targetId) {
        // PENDING이 하나라도 있으면 PENDING 반환
        boolean hasPending = queryFactory
                .select(report.id)
                .from(report)
                .where(
                        report.targetType.eq(targetType),
                        report.targetId.eq(targetId),
                        report.status.eq(ReportStatus.PENDING)
                )
                .fetchFirst() != null;

        return hasPending ? ReportStatus.PENDING : ReportStatus.RESOLVED;
    }

    @Override
    public List<Report> findByTargetTypeAndTargetId(ReportTargetType targetType, Long targetId) {
        return queryFactory
                .selectFrom(report)
                .where(
                        report.targetType.eq(targetType),
                        report.targetId.eq(targetId)
                )
                .orderBy(report.createdAt.asc())
                .fetch();
    }
}
