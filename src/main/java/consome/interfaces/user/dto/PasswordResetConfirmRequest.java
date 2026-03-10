package consome.interfaces.user.dto;

import jakarta.validation.constraints.NotBlank;

public record PasswordResetConfirmRequest(
        @NotBlank(message = "토큰을 입력해주세요")
        String token,

        @NotBlank(message = "새 비밀번호를 입력해주세요")
        String newPassword
) {
}
