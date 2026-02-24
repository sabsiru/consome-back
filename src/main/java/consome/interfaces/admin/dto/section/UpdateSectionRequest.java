package consome.interfaces.admin.dto.section;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdateSectionRequest(
        @NotBlank(message = "섹션 이름은 필수입니다.")
        @Size(min = 1, max = 20, message = "섹션 이름은 1자 이상 20자 이하입니다.")
        String name
) {
}
