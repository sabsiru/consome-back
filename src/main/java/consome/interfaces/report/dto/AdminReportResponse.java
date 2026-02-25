package consome.interfaces.report.dto;

import consome.application.report.ReportResult;
import consome.domain.report.entity.ReportReason;
import consome.domain.report.entity.ReportStatus;
import consome.domain.report.entity.ReportTargetType;
import consome.domain.user.SuspensionType;

import java.time.LocalDateTime;

public record AdminReportResponse(
        Long id,
        Long reporterId,
        String reporterNickname,
        ReportTargetType targetType,
        Long targetId,
        Long targetUserId,
        String targetUserNickname,
        String targetContent,
        Long boardId,
        Long postId,
        ReportReason reason,
        String description,
        ReportStatus status,
        SuspensionType suspensionType,
        LocalDateTime createdAt,
        LocalDateTime resolvedAt
) {
    public static AdminReportResponse from(ReportResult result) {
        return new AdminReportResponse(
                result.id(),
                result.reporterId(),
                result.reporterNickname(),
                result.targetType(),
                result.targetId(),
                result.targetUserId(),
                result.targetUserNickname(),
                result.targetContent(),
                result.boardId(),
                result.postId(),
                result.reason(),
                result.description(),
                result.status(),
                result.suspensionType(),
                result.createdAt(),
                result.resolvedAt()
        );
    }
}
