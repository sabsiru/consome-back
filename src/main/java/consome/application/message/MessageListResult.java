package consome.application.message;

import consome.domain.message.entity.Message;

import java.time.LocalDateTime;

public record MessageListResult(
        Long id,
        Long otherUserId,
        String otherUserNickname,
        String contentPreview,
        int point,
        boolean isRead,
        LocalDateTime createdAt
) {
    public static MessageListResult forReceived(Message message, String senderNickname) {
        return new MessageListResult(
                message.getId(),
                message.getSenderId(),
                senderNickname,
                truncateContent(message.getContent()),
                message.getPoint(),
                message.isRead(),
                message.getCreatedAt()
        );
    }

    public static MessageListResult forSent(Message message, String receiverNickname) {
        return new MessageListResult(
                message.getId(),
                message.getReceiverId(),
                receiverNickname,
                truncateContent(message.getContent()),
                message.getPoint(),
                message.isRead(),
                message.getCreatedAt()
        );
    }

    private static String truncateContent(String content) {
        if (content.length() <= 50) {
            return content;
        }
        return content.substring(0, 50) + "...";
    }
}
