package consome.domain.report.exception;

import lombok.Getter;

@Getter
public abstract class ReportException extends RuntimeException {
    private final String code;

    protected ReportException(String code, String message) {
        super(message);
        this.code = code;
    }

    public static class NotFound extends ReportException {
        public NotFound(Long reportId) {
            super("REPORT_NOT_FOUND", "신고를 찾을 수 없습니다: " + reportId);
        }
    }

    public static class SelfReport extends ReportException {
        public SelfReport() {
            super("SELF_REPORT", "본인 콘텐츠는 신고할 수 없습니다.");
        }
    }

    public static class AlreadyReported extends ReportException {
        public AlreadyReported() {
            super("ALREADY_REPORTED", "이미 신고한 대상입니다.");
        }
    }

    public static class TargetNotFound extends ReportException {
        public TargetNotFound() {
            super("TARGET_NOT_FOUND", "신고 대상을 찾을 수 없습니다.");
        }
    }

    public static class AlreadyProcessed extends ReportException {
        public AlreadyProcessed() {
            super("ALREADY_PROCESSED", "이미 처리된 신고입니다.");
        }
    }
}
