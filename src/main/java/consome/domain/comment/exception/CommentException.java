package consome.domain.comment.exception;

import lombok.Getter;

@Getter
public abstract class CommentException extends RuntimeException {
    private final String code;

    protected CommentException(String code, String message) {
        super(message);
        this.code = code;
    }

    public static class NotFound extends CommentException {
        public NotFound(Long commentId) {
            super("COMMENT_NOT_FOUND", "댓글을 찾을 수 없습니다: " + commentId);
        }

        public NotFound() {
            super("COMMENT_NOT_FOUND", "잘못된 댓글입니다.");
        }
    }

    public static class StatsNotFound extends CommentException {
        public StatsNotFound(Long commentId) {
            super("COMMENT_STATS_NOT_FOUND", "댓글 통계를 찾을 수 없습니다: " + commentId);
        }
    }

    public static class Unauthorized extends CommentException {
        public Unauthorized(String action) {
            super("COMMENT_UNAUTHORIZED", "작성자만 댓글을 " + action + "할 수 있습니다.");
        }
    }

    public static class AlreadyDeleted extends CommentException {
        public AlreadyDeleted() {
            super("COMMENT_ALREADY_DELETED", "삭제된 댓글은 수정할 수 없습니다.");
        }
    }

    public static class AlreadyLiked extends CommentException {
        public AlreadyLiked() {
            super("COMMENT_ALREADY_LIKED", "이미 추천했습니다.");
        }
    }

    public static class AlreadyDisliked extends CommentException {
        public AlreadyDisliked() {
            super("COMMENT_ALREADY_DISLIKED", "이미 비추천했습니다.");
        }
    }
}
