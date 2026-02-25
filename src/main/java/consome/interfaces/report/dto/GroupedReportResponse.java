package consome.interfaces.report.dto;

import consome.domain.report.entity.ReportStatus;
import consome.domain.report.entity.ReportTargetType;
import consome.domain.user.SuspensionType;

import java.time.LocalDateTime;

public record GroupedReportResponse(
        ReportTargetType targetType,
        Long targetId,
        Long targetUserId,
        String targetUserNickname,
        String targetContent,
        Long boardId,
        Long postId,
        ReportStatus status,
        SuspensionType suspensionType,
        LocalDateTime suspensionEndAt,
        int reportCount,
        String firstReporterNickname,
        int otherReporterCount,
        LocalDateTime firstReportedAt,
        LocalDateTime lastReportedAt
) {
}
