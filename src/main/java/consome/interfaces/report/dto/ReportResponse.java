package consome.interfaces.report.dto;

import consome.application.report.ReportResult;
import consome.domain.report.entity.ReportReason;
import consome.domain.report.entity.ReportStatus;
import consome.domain.report.entity.ReportTargetType;

import java.time.LocalDateTime;

public record ReportResponse(
        Long id,
        ReportTargetType targetType,
        Long targetId,
        ReportReason reason,
        String description,
        ReportStatus status,
        LocalDateTime createdAt
) {
    public static ReportResponse from(ReportResult result) {
        return new ReportResponse(
                result.id(),
                result.targetType(),
                result.targetId(),
                result.reason(),
                result.description(),
                result.status(),
                result.createdAt()
        );
    }
}
