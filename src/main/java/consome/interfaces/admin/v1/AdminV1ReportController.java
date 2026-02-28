package consome.interfaces.admin.v1;

import consome.application.admin.AdminReportFacade;
import consome.domain.report.entity.ReportStatus;
import consome.domain.report.entity.ReportTargetType;
import consome.domain.user.SuspensionType;
import consome.interfaces.report.dto.AdminReportResponse;
import consome.interfaces.report.dto.GroupedReportResponse;
import consome.interfaces.report.dto.ReporterDetailResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/admin/reports")
public class AdminV1ReportController {

    private final AdminReportFacade adminReportFacade;

    @GetMapping
    public ResponseEntity<Page<GroupedReportResponse>> findAllGrouped(
            @RequestParam(required = false) ReportStatus status,
            @PageableDefault(size = 50) Pageable pageable) {
        var result = adminReportFacade.findAllGrouped(status, pageable);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/{targetType}/{targetId}/reporters")
    public ResponseEntity<List<ReporterDetailResponse>> findReportersByTarget(
            @PathVariable ReportTargetType targetType,
            @PathVariable Long targetId) {
        var result = adminReportFacade.findReportersByTarget(targetType, targetId);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/all")
    public ResponseEntity<Page<AdminReportResponse>> findAll(
            @RequestParam(required = false) ReportStatus status,
            @RequestParam(required = false) ReportTargetType targetType,
            @RequestParam(required = false) Long targetUserId,
            @PageableDefault(size = 50) Pageable pageable) {
        var result = adminReportFacade.findAll(status, targetType, targetUserId, pageable);
        return ResponseEntity.ok(result.map(AdminReportResponse::from));
    }

    @GetMapping("/{reportId}")
    public ResponseEntity<AdminReportResponse> findById(@PathVariable Long reportId) {
        var result = adminReportFacade.findById(reportId);
        return ResponseEntity.ok(AdminReportResponse.from(result));
    }

    @PatchMapping("/{reportId}/resolve")
    public ResponseEntity<AdminReportResponse> resolve(
            @PathVariable Long reportId,
            @RequestParam Long userId,
            @RequestParam(required = false) SuspensionType suspensionType) {
        var result = adminReportFacade.resolve(reportId, userId, suspensionType);
        return ResponseEntity.ok(AdminReportResponse.from(result));
    }

    @PatchMapping("/{reportId}/reject")
    public ResponseEntity<AdminReportResponse> reject(
            @PathVariable Long reportId,
            @RequestParam Long userId) {
        var result = adminReportFacade.reject(reportId, userId);
        return ResponseEntity.ok(AdminReportResponse.from(result));
    }
}
