package consome.interfaces.message.dto;

import consome.application.message.MessageListResult;

import java.time.LocalDateTime;

public record MessageListResponse(
        Long id,
        Long otherUserId,
        String otherUserNickname,
        String contentPreview,
        int point,
        boolean isRead,
        LocalDateTime createdAt
) {
    public static MessageListResponse from(MessageListResult result) {
        return new MessageListResponse(
                result.id(),
                result.otherUserId(),
                result.otherUserNickname(),
                result.contentPreview(),
                result.point(),
                result.isRead(),
                result.createdAt()
        );
    }
}
