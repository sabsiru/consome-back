package consome.interfaces.message.dto;

import consome.application.message.MessageResult;

import java.time.LocalDateTime;

public record MessageResponse(
        Long id,
        Long senderId,
        String senderNickname,
        Long receiverId,
        String receiverNickname,
        String content,
        int point,
        boolean isRead,
        LocalDateTime createdAt,
        LocalDateTime readAt
) {
    public static MessageResponse from(MessageResult result) {
        return new MessageResponse(
                result.id(),
                result.senderId(),
                result.senderNickname(),
                result.receiverId(),
                result.receiverNickname(),
                result.content(),
                result.point(),
                result.isRead(),
                result.createdAt(),
                result.readAt()
        );
    }
}
