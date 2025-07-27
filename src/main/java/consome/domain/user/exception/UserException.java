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

    public static class DuplicateNickname extends UserException {
        private final String nickname;

        public DuplicateNickname(String nickname) {
            super("DUPLICATE_NICKNAME","이미 사용 중인 닉네임입니다.");
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
}
