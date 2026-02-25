package consome.domain.report;

import consome.domain.report.entity.Report;
import consome.domain.report.entity.ReportReason;
import consome.domain.report.entity.ReportStatus;
import consome.domain.report.entity.ReportTargetType;
import consome.domain.report.exception.ReportException;
import consome.domain.report.repository.ReportQueryRepository;
import consome.domain.report.repository.ReportRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ReportService {

    private final ReportRepository reportRepository;
    private final ReportQueryRepository reportQueryRepository;

    @Transactional
    public Report create(Long reporterId, ReportTargetType targetType, Long targetId,
                         Long targetUserId, ReportReason reason, String description) {
        validateNotDuplicate(reporterId, targetType, targetId);

        Report report = Report.create(reporterId, targetType, targetId, targetUserId, reason, description);
        return reportRepository.save(report);
    }

    @Transactional(readOnly = true)
    public Page<Report> findAllWithFilters(ReportStatus status, ReportTargetType targetType, Long targetUserId, Pageable pageable) {
        return reportQueryRepository.findAllWithFilters(status, targetType, targetUserId, pageable);
    }

    @Transactional(readOnly = true)
    public Report findById(Long reportId) {
        return reportRepository.findById(reportId)
                .orElseThrow(() -> new ReportException.NotFound(reportId));
    }

    @Transactional
    public Report resolve(Long reportId, Long adminId, consome.domain.user.SuspensionType suspensionType) {
        Report report = findById(reportId);
        validatePending(report);
        report.resolve(adminId, suspensionType);
        return report;
    }

    @Transactional
    public Report reject(Long reportId, Long adminId) {
        Report report = findById(reportId);
        validatePending(report);
        report.reject(adminId);
        return report;
    }

    private void validateNotDuplicate(Long reporterId, ReportTargetType targetType, Long targetId) {
        if (reportRepository.existsByReporterIdAndTargetTypeAndTargetId(reporterId, targetType, targetId)) {
            throw new ReportException.AlreadyReported();
        }
    }

    private void validatePending(Report report) {
        if (!report.isPending()) {
            throw new ReportException.AlreadyProcessed();
        }
    }
}
