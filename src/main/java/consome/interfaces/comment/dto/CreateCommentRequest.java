package consome.interfaces.comment.dto;

import jakarta.validation.constraints.NotBlank;

public record CreateCommentRequest(
        @NotBlank Long userId,
        @NotBlank Long parentId,
        @NotBlank String content
) {
}
