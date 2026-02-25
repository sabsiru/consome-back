package consome.interfaces.admin.dto;

import consome.domain.user.SuspensionType;
import jakarta.validation.constraints.NotNull;

public record SuspendUserRequest(
        @NotNull(message = "정지 유형은 필수입니다.")
        SuspensionType type,
        String reason
) {
}
