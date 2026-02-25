package consome.interfaces.report.dto;

import consome.application.report.CreateReportCommand;
import consome.domain.report.entity.ReportReason;
import consome.domain.report.entity.ReportTargetType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateReportRequest(
        @NotNull(message = "신고 대상 타입은 필수입니다.")
        ReportTargetType targetType,

        @NotNull(message = "신고 대상 ID는 필수입니다.")
        Long targetId,

        @NotNull(message = "신고 사유는 필수입니다.")
        ReportReason reason,

        @Size(max = 500, message = "상세 설명은 500자를 초과할 수 없습니다.")
        String description
) {
    public CreateReportCommand toCommand(Long reporterId) {
        return new CreateReportCommand(reporterId, targetType, targetId, reason, description);
    }
}
