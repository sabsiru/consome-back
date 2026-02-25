package consome.domain.report.repository;

import consome.domain.report.entity.Report;
import consome.domain.report.entity.ReportStatus;
import consome.domain.report.entity.ReportTargetType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ReportQueryRepository {

    Page<Report> findAllWithFilters(ReportStatus status, ReportTargetType targetType, Long targetUserId, Pageable pageable);

    Page<GroupedReportProjection> findAllGrouped(ReportStatus status, Pageable pageable);

    List<Report> findByTargetTypeAndTargetId(ReportTargetType targetType, Long targetId);

    record GroupedReportProjection(
            ReportTargetType targetType,
            Long targetId,
            Long firstReportId,
            int reportCount,
            ReportStatus representativeStatus
    ) {}
}
