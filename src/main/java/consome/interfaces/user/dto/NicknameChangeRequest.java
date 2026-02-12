package consome.interfaces.user.dto;

import jakarta.validation.constraints.NotBlank;

public record NicknameChangeRequest(
        @NotBlank(message = "새 닉네임을 입력해주세요")
        String nickname
) {
}
