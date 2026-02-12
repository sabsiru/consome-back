package consome.domain.common.exception;

import lombok.Getter;

@Getter
public class BusinessException extends RuntimeException {
    private final String code;

    public BusinessException(String code, String message) {
        super(message);
        this.code = code;
    }

    // Admin 관련
    public static class InvalidAdminAction extends BusinessException {
        public InvalidAdminAction(String message) {
            super("INVALID_ADMIN_ACTION", message);
        }
    }

    // Board 관련
    public static class BoardNotFound extends BusinessException {
        public BoardNotFound(Long boardId) {
            super("BOARD_NOT_FOUND", "게시판을 찾을 수 없습니다: " + boardId);
        }
    }

    // Category 관련
    public static class CategoryNotFound extends BusinessException {
        public CategoryNotFound(Long categoryId) {
            super("CATEGORY_NOT_FOUND", "카테고리를 찾을 수 없습니다: " + categoryId);
        }
    }

    // Point 관련
    public static class InsufficientPoints extends BusinessException {
        public InsufficientPoints(int required, int available) {
            super("INSUFFICIENT_POINTS",
                String.format("포인트가 부족합니다. 필요: %d, 보유: %d", required, available));
        }
    }

    public static class InvalidPointAmount extends BusinessException {
        public InvalidPointAmount(String message) {
            super("INVALID_POINT_AMOUNT", message);
        }
    }

    // Password 관련
    public static class InvalidPassword extends BusinessException {
        public InvalidPassword(String message) {
            super("INVALID_PASSWORD", message);
        }
    }
}
