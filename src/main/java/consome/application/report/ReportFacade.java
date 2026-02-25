package consome.application.report;

import consome.domain.comment.CommentService;
import consome.domain.comment.Comment;
import consome.domain.post.PostService;
import consome.domain.post.entity.Post;
import consome.domain.report.ReportService;
import consome.domain.report.entity.Report;
import consome.domain.report.entity.ReportTargetType;
import consome.domain.report.exception.ReportException;
import consome.domain.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ReportFacade {

    private final ReportService reportService;
    private final PostService postService;
    private final CommentService commentService;
    private final UserService userService;

    @Transactional
    public ReportResult create(CreateReportCommand command) {
        Long targetOwnerId = validateAndGetTargetOwnerId(command.targetType(), command.targetId());

        // 본인 콘텐츠 신고 금지
        if (command.reporterId().equals(targetOwnerId)) {
            throw new ReportException.SelfReport();
        }

        Report report = reportService.create(
                command.reporterId(),
                command.targetType(),
                command.targetId(),
                targetOwnerId,
                command.reason(),
                command.description()
        );

        return ReportResult.from(report);
    }

    private Long validateAndGetTargetOwnerId(ReportTargetType targetType, Long targetId) {
        return switch (targetType) {
            case POST -> {
                Post post = postService.getPost(targetId);
                yield post.getUserId();
            }
            case COMMENT -> {
                Comment comment = commentService.findById(targetId);
                yield comment.getUserId();
            }
            case USER -> {
                userService.findById(targetId);
                yield targetId;
            }
        };
    }
}
