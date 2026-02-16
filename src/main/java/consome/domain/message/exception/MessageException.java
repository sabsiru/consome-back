package consome.domain.message.exception;

import lombok.Getter;

@Getter
public abstract class MessageException extends RuntimeException {
    private final String code;

    protected MessageException(String code, String message) {
        super(message);
        this.code = code;
    }

    public static class NotFound extends MessageException {
        public NotFound(Long messageId) {
            super("MESSAGE_NOT_FOUND", "쪽지를 찾을 수 없습니다: " + messageId);
        }
    }

    public static class AccessDenied extends MessageException {
        public AccessDenied() {
            super("MESSAGE_ACCESS_DENIED", "해당 쪽지에 접근할 권한이 없습니다.");
        }
    }

    public static class BlockedUser extends MessageException {
        public BlockedUser() {
            super("BLOCKED_USER", "차단된 사용자에게 쪽지를 보낼 수 없습니다.");
        }
    }

    public static class CannotSendToSelf extends MessageException {
        public CannotSendToSelf() {
            super("CANNOT_SEND_TO_SELF", "자기 자신에게 쪽지를 보낼 수 없습니다.");
        }
    }

    public static class InsufficientPoint extends MessageException {
        public InsufficientPoint() {
            super("INSUFFICIENT_POINT", "포인트가 부족합니다.");
        }
    }

    public static class AlreadyBlocked extends MessageException {
        public AlreadyBlocked() {
            super("ALREADY_BLOCKED", "이미 차단한 사용자입니다.");
        }
    }

    public static class NotBlocked extends MessageException {
        public NotBlocked() {
            super("NOT_BLOCKED", "차단하지 않은 사용자입니다.");
        }
    }
}
