package consome.domain.post.exception;

import lombok.Getter;

@Getter
public abstract class PostException extends RuntimeException {
    private final String code;

    protected PostException(String code, String message) {
        super(message);
        this.code = code;
    }

    public static class NotFound extends PostException {
        public NotFound(Long postId) {
            super("POST_NOT_FOUND", "게시글을 찾을 수 없습니다: " + postId);
        }

        public NotFound() {
            super("POST_NOT_FOUND", "게시글을 찾을 수 없습니다.");
        }
    }

    public static class Unauthorized extends PostException {
        public Unauthorized(String action) {
            super("POST_UNAUTHORIZED", "작성자만 게시글을 " + action + "할 수 있습니다.");
        }
    }

    public static class AlreadyLiked extends PostException {
        public AlreadyLiked() {
            super("POST_ALREADY_LIKED", "이미 좋아요를 누른 게시글입니다.");
        }
    }

    public static class AlreadyDisliked extends PostException {
        public AlreadyDisliked() {
            super("POST_ALREADY_DISLIKED", "이미 싫어요를 누른 게시글입니다.");
        }
    }

    public static class NotLiked extends PostException {
        public NotLiked() {
            super("POST_NOT_LIKED", "좋아요를 누르지 않았습니다.");
        }
    }

    public static class NotDisliked extends PostException {
        public NotDisliked() {
            super("POST_NOT_DISLIKED", "싫어요를 누르지 않았습니다.");
        }
    }
}
