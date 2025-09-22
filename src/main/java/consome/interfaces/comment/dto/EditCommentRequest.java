package consome.interfaces.comment.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record EditCommentRequest(
        Long userId,
        @NotBlank @Size(max = 500) String content
) {
}
