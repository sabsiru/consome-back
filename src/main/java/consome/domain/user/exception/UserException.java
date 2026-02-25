package consome.domain.user.exception;

import lombok.Getter;

@Getter
public abstract class UserException extends RuntimeException {
    private final String code;

    protected UserException(String code, String message) {
        super(message);
        this.code = code;
    }

    public static class DuplicateLoginId extends UserException {
        private final String loginId;

        public DuplicateLoginId(String loginId) {
            super("DUPLICATE_LOGIN_ID", "이미 사용 중인 아이디입니다.");
            this.loginId = loginId;
        }
    }

    public static class InvalidLoginIdLength extends UserException {
        private final String loginId;

        public InvalidLoginIdLength(String loginId) {
            super("INVALID_LOGIN_ID_LENGTH", "로그인 아이디는 4자 이상 20자 이하의 영문자 또는 숫자만 입력 가능합니다.");
            this.loginId = loginId;
        }
    }

    public static class InvalidLoginIdFormat extends UserException {
        private final String loginId;

        public InvalidLoginIdFormat(String loginId) {
            super("INVALID_LOGIN_ID_FORMAT", "로그인 아이디는 영문 대소문자와 숫자만 포함해야 합니다.");
            this.loginId = loginId;
        }
    }

    public static class DuplicateNickname extends UserException {
        private final String nickname;

        public DuplicateNickname(String nickname) {
            super("DUPLICATE_NICKNAME", "이미 사용 중인 닉네임입니다.");
            this.nickname = nickname;
        }
    }

    public static class InvalidNicknameLength extends UserException {
        private final String nickname;

        public InvalidNicknameLength(String nickname) {
            super("INVALID_NICKNAME_LENGTH", "닉네임은 2자 이상 20자 이하로 입력해주세요.");
            this.nickname = nickname;
        }
    }

    public static class InvalidNicknameFormat extends UserException {
        private final String nickname;

        public InvalidNicknameFormat(String nickname) {
            super("INVALID_NICKNAME_FORMAT", "닉네임은 한글, 영문 대소문자, 숫자만 포함해야 합니다.");
            this.nickname = nickname;
        }
    }

    public static class loginFailure extends UserException {
        private final String loginId;
        private final String password;

        public loginFailure(String loginId, String password) {
            super("INVALID_CREDENTIALS", "아이디 또는 비밀번호가 일치하지 않습니다");
            this.loginId = loginId;
            this.password = password;
        }

    }
    public static class NotFound extends UserException {
        public NotFound(String message) {
            super("USER_NOT_FOUND", message);
        }
    }

    public static class PasswordMismatch extends UserException {
        public PasswordMismatch(String message) {
            super("PASSWORD_MISMATCH", message);
        }
    }

    public static class InsufficientPoint extends UserException {
        public InsufficientPoint(String message) {
            super("INSUFFICIENT_POINT", message);
        }
    }

    public static class Suspended extends UserException {
        public Suspended(String reason, String until) {
            super("USER_SUSPENDED", "계정이 정지되었습니다. 사유: " + reason + " (해제: " + until + ")");
        }
    }

    public static class Banned extends UserException {
        public Banned(String reason) {
            super("USER_BANNED", "계정이 영구 정지되었습니다. 사유: " + reason);
        }
    }

}
