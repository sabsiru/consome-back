package consome.domain.auth;

import consome.domain.common.exception.BusinessException;

public class PasswordPolicy {

    public static void validate(String rawPassword) {
        if (rawPassword.length() < 8 || rawPassword.length() > 20) {
            throw new BusinessException.InvalidPassword("비밀번호는 8자 이상 20자 이하이어야 합니다.");
        }
        if (!rawPassword.matches("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).+$")) {
            throw new BusinessException.InvalidPassword("비밀번호는 영문 대소문자와 숫자를 포함해야 합니다.");
        }
    }
}
