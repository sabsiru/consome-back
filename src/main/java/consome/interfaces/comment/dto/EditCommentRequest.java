package consome.interfaces.comment.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record EditCommentRequest(
        @NotBlank @Size(max = 500) String content
) {
}
