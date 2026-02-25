package consome.application.report;

import consome.domain.report.entity.Report;
import consome.domain.report.entity.ReportReason;
import consome.domain.report.entity.ReportStatus;
import consome.domain.report.entity.ReportTargetType;
import consome.domain.user.SuspensionType;

import java.time.LocalDateTime;

public record ReportResult(
        Long id,
        Long reporterId,
        String reporterNickname,
        ReportTargetType targetType,
        Long targetId,
        Long targetUserId,          // 신고 대상 작성자 ID
        String targetUserNickname,  // 신고 대상 작성자 닉네임
        String targetContent,       // 게시글 제목 or 댓글 내용
        Long boardId,               // 게시글/댓글의 게시판 ID
        Long postId,                // 게시글 ID 또는 댓글의 게시글 ID
        ReportReason reason,
        String description,
        ReportStatus status,
        SuspensionType suspensionType,
        LocalDateTime createdAt,
        LocalDateTime resolvedAt
) {
    public static ReportResult from(Report report, String reporterNickname,
                                     Long targetUserId, String targetUserNickname,
                                     String targetContent, Long boardId, Long postId) {
        return new ReportResult(
                report.getId(),
                report.getReporterId(),
                reporterNickname,
                report.getTargetType(),
                report.getTargetId(),
                targetUserId,
                targetUserNickname,
                targetContent,
                boardId,
                postId,
                report.getReason(),
                report.getDescription(),
                report.getStatus(),
                report.getSuspensionType(),
                report.getCreatedAt(),
                report.getResolvedAt()
        );
    }

    public static ReportResult from(Report report) {
        return from(report, null, null, null, null, null, null);
    }
}
