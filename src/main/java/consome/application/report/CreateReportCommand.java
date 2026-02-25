package consome.application.report;

import consome.domain.report.entity.ReportReason;
import consome.domain.report.entity.ReportTargetType;

public record CreateReportCommand(
        Long reporterId,
        ReportTargetType targetType,
        Long targetId,
        ReportReason reason,
        String description
) {
}
