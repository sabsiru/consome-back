package consome.interfaces.post.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record EditRequest(
        @NotBlank(message = "제목을 입력해주세요.")
        @Size(max = 100, message = "제목은 100자 이하로 입력해주세요.")
        String title,

        Long categoryId,

        @NotBlank(message = "내용을 입력해주세요.")
        @Size(max = 50000, message = "내용이 너무 깁니다.")
        String content
) {
}
