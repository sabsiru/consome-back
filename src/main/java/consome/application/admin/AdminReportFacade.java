package consome.application.admin;

import consome.application.report.ReportResult;
import consome.domain.comment.Comment;
import consome.domain.comment.CommentService;
import consome.domain.post.PostService;
import consome.domain.post.entity.Post;
import consome.domain.report.ReportService;
import consome.domain.report.entity.Report;
import consome.domain.report.entity.ReportStatus;
import consome.domain.report.entity.ReportTargetType;
import consome.domain.report.repository.ReportQueryRepository;
import consome.domain.report.repository.ReportQueryRepository.GroupedReportProjection;
import consome.domain.user.SuspensionType;
import consome.domain.user.User;
import consome.domain.user.UserService;
import consome.interfaces.report.dto.GroupedReportResponse;
import consome.interfaces.report.dto.ReporterDetailResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminReportFacade {

    private final ReportService reportService;
    private final ReportQueryRepository reportQueryRepository;
    private final UserService userService;
    private final PostService postService;
    private final CommentService commentService;

    @Transactional(readOnly = true)
    public Page<ReportResult> findAll(ReportStatus status, ReportTargetType targetType, Long targetUserId, Pageable pageable) {
        return reportService.findAllWithFilters(status, targetType, targetUserId, pageable)
                .map(this::toResult);
    }

    @Transactional(readOnly = true)
    public ReportResult findById(Long reportId) {
        Report report = reportService.findById(reportId);
        return toResult(report);
    }

    @Transactional
    public ReportResult resolve(Long reportId, Long adminId, consome.domain.user.SuspensionType suspensionType) {
        Report report = reportService.resolve(reportId, adminId, suspensionType);

        // 제재 이력 저장 및 유저 정지 처리
        if (report.getTargetUserId() != null) {
            String reason = report.getReason().getDescription();
            userService.suspend(report.getTargetUserId(), suspensionType, reason, reportId, adminId);
        }

        return toResult(report);
    }

    @Transactional
    public ReportResult reject(Long reportId, Long adminId) {
        Report report = reportService.reject(reportId, adminId);
        return toResult(report);
    }

    private ReportResult toResult(Report report) {
        String reporterNickname = userService.getNicknameById(report.getReporterId());

        Long targetUserId = null;
        String targetUserNickname = null;
        String targetContent = null;
        Long boardId = null;
        Long postId = null;

        try {
            switch (report.getTargetType()) {
                case POST -> {
                    Post post = postService.getPost(report.getTargetId());
                    targetUserId = post.getUserId();
                    targetUserNickname = userService.getNicknameById(post.getUserId());
                    targetContent = post.getTitle();
                    boardId = post.getBoardId();
                    postId = post.getId();
                }
                case COMMENT -> {
                    Comment comment = commentService.findById(report.getTargetId());
                    targetUserId = comment.getUserId();
                    targetUserNickname = userService.getNicknameById(comment.getUserId());
                    targetContent = comment.getContent();
                    Post post = postService.getPost(comment.getPostId());
                    boardId = post.getBoardId();
                    postId = comment.getPostId();
                }
                case USER -> {
                    User user = userService.findById(report.getTargetId());
                    targetUserId = user.getId();
                    targetUserNickname = user.getNickname();
                }
            }
        } catch (Exception e) {
            // 삭제된 대상일 수 있음
        }

        return ReportResult.from(report, reporterNickname, targetUserId, targetUserNickname, targetContent, boardId, postId);
    }

    @Transactional(readOnly = true)
    public Page<GroupedReportResponse> findAllGrouped(ReportStatus status, Pageable pageable) {
        return reportQueryRepository.findAllGrouped(status, pageable)
                .map(this::toGroupedResponse);
    }

    @Transactional(readOnly = true)
    public List<ReporterDetailResponse> findReportersByTarget(ReportTargetType targetType, Long targetId) {
        List<Report> reports = reportQueryRepository.findByTargetTypeAndTargetId(targetType, targetId);
        return reports.stream()
                .map(r -> new ReporterDetailResponse(
                        r.getId(),
                        r.getReporterId(),
                        userService.getNicknameById(r.getReporterId()),
                        r.getReason(),
                        r.getDescription(),
                        r.getCreatedAt()
                ))
                .toList();
    }

    private GroupedReportResponse toGroupedResponse(GroupedReportProjection projection) {
        List<Report> reports = reportQueryRepository.findByTargetTypeAndTargetId(
                projection.targetType(), projection.targetId());

        Report firstReport = reports.get(0);
        Report lastReport = reports.get(reports.size() - 1);

        Long targetUserId = null;
        String targetUserNickname = null;
        String targetContent = null;
        Long boardId = null;
        Long postId = null;

        try {
            switch (projection.targetType()) {
                case POST -> {
                    Post post = postService.getPost(projection.targetId());
                    targetUserId = post.getUserId();
                    targetUserNickname = userService.getNicknameById(post.getUserId());
                    targetContent = post.getTitle();
                    boardId = post.getBoardId();
                    postId = post.getId();
                }
                case COMMENT -> {
                    Comment comment = commentService.findById(projection.targetId());
                    targetUserId = comment.getUserId();
                    targetUserNickname = userService.getNicknameById(comment.getUserId());
                    targetContent = comment.getContent();
                    Post post = postService.getPost(comment.getPostId());
                    boardId = post.getBoardId();
                    postId = comment.getPostId();
                }
                case USER -> {
                    User user = userService.findById(projection.targetId());
                    targetUserId = user.getId();
                    targetUserNickname = user.getNickname();
                }
            }
        } catch (Exception e) {
            // 삭제된 대상일 수 있음
        }

        String firstReporterNickname = userService.getNicknameById(firstReport.getReporterId());
        int otherReporterCount = projection.reportCount() - 1;

        // 가장 긴 제재 찾기 (PERMANENT가 가장 김)
        Report maxSuspensionReport = reports.stream()
                .filter(r -> r.getSuspensionType() != null)
                .max((a, b) -> {
                    if (a.getSuspensionType().isPermanent()) return 1;
                    if (b.getSuspensionType().isPermanent()) return -1;
                    return Integer.compare(a.getSuspensionType().getDays(), b.getSuspensionType().getDays());
                })
                .orElse(null);

        SuspensionType suspensionType = maxSuspensionReport != null ? maxSuspensionReport.getSuspensionType() : null;
        LocalDateTime suspensionEndAt = null;
        if (maxSuspensionReport != null && !suspensionType.isPermanent()) {
            suspensionEndAt = maxSuspensionReport.getResolvedAt().plusDays(suspensionType.getDays());
        }

        return new GroupedReportResponse(
                projection.targetType(),
                projection.targetId(),
                targetUserId,
                targetUserNickname,
                targetContent,
                boardId,
                postId,
                projection.representativeStatus(),
                suspensionType,
                suspensionEndAt,
                projection.reportCount(),
                firstReporterNickname,
                otherReporterCount,
                firstReport.getCreatedAt(),
                lastReport.getCreatedAt()
        );
    }
}
