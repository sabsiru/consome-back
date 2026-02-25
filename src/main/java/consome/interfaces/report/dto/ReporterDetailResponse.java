package consome.interfaces.report.dto;

import consome.domain.report.entity.ReportReason;

import java.time.LocalDateTime;

public record ReporterDetailResponse(
        Long reportId,
        Long reporterId,
        String reporterNickname,
        ReportReason reason,
        String description,
        LocalDateTime createdAt
) {
}
