package consome.application.message;

import consome.domain.message.entity.Message;

import java.time.LocalDateTime;

public record MessageResult(
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
    public static MessageResult from(Message message, String senderNickname, String receiverNickname) {
        return new MessageResult(
                message.getId(),
                message.getSenderId(),
                senderNickname,
                message.getReceiverId(),
                receiverNickname,
                message.getContent(),
                message.getPoint(),
                message.isRead(),
                message.getCreatedAt(),
                message.getReadAt()
        );
    }
}
