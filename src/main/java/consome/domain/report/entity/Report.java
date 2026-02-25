package consome.domain.report.entity;

import consome.domain.user.SuspensionType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(indexes = {
        @Index(name = "idx_report_status", columnList = "status"),
        @Index(name = "idx_report_target", columnList = "targetType, targetId"),
        @Index(name = "idx_report_reporter_target", columnList = "reporterId, targetType, targetId")
})
public class Report {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long reporterId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ReportTargetType targetType;

    @Column(nullable = false)
    private Long targetId;

    @Column
    private Long targetUserId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ReportReason reason;

    @Column(length = 500)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ReportStatus status = ReportStatus.PENDING;

    private Long resolvedBy;

    private LocalDateTime resolvedAt;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private SuspensionType suspensionType;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    private Report(Long reporterId, ReportTargetType targetType, Long targetId,
                   Long targetUserId, ReportReason reason, String description) {
        this.reporterId = reporterId;
        this.targetType = targetType;
        this.targetId = targetId;
        this.targetUserId = targetUserId;
        this.reason = reason;
        this.description = description;
        this.createdAt = LocalDateTime.now();
    }

    public static Report create(Long reporterId, ReportTargetType targetType, Long targetId,
                                 Long targetUserId, ReportReason reason, String description) {
        validateDescription(description);
        return new Report(reporterId, targetType, targetId, targetUserId, reason, description);
    }

    private static void validateDescription(String description) {
        if (description != null && description.length() > 500) {
            throw new IllegalArgumentException("신고 상세 설명은 500자를 초과할 수 없습니다.");
        }
    }

    public void resolve(Long adminId, SuspensionType suspensionType) {
        this.status = ReportStatus.RESOLVED;
        this.resolvedBy = adminId;
        this.resolvedAt = LocalDateTime.now();
        this.suspensionType = suspensionType;
    }

    public void reject(Long adminId) {
        this.status = ReportStatus.REJECTED;
        this.resolvedBy = adminId;
        this.resolvedAt = LocalDateTime.now();
    }

    public boolean isPending() {
        return this.status == ReportStatus.PENDING;
    }
}
