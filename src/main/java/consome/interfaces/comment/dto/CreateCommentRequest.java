package consome.interfaces.comment.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateCommentRequest(
        Long parentId,

        @NotBlank(message = "댓글 내용을 입력해주세요.")
        @Size(max = 2000, message = "댓글은 2000자 이하로 입력해주세요.")
        String content
) {
}
