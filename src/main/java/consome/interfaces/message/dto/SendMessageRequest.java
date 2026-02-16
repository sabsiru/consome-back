package consome.interfaces.message.dto;

import consome.application.message.SendMessageCommand;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

public record SendMessageRequest(
        @NotNull(message = "수신자 ID는 필수입니다.")
        Long receiverId,

        @NotBlank(message = "쪽지 내용은 필수입니다.")
        @Size(max = 2000, message = "쪽지 내용은 2000자를 초과할 수 없습니다.")
        String content,

        @PositiveOrZero(message = "포인트는 0 이상이어야 합니다.")
        int point
) {
    public SendMessageCommand toCommand(Long senderId) {
        return new SendMessageCommand(senderId, receiverId, content, point);
    }
}
