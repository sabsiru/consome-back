package consome.interfaces.notification.dto;

import consome.application.notification.NotificationResult;
import consome.domain.notification.NotificationType;

import java.time.LocalDateTime;

public record NotificationResponse(
        Long id,
        NotificationType type,
        Long actorId,
        String actorNickname,
        Long targetId,
        Long relatedId,
        Long referenceId,
        String message,
        boolean isRead,
        LocalDateTime createdAt,
        LocalDateTime readAt
) {
    public static NotificationResponse from(NotificationResult result) {
        return new NotificationResponse(
                result.id(),
                result.type(),
                result.actorId(),
                result.actorNickname(),
                result.targetId(),
                result.relatedId(),
                result.referenceId(),
                result.message(),
                result.isRead(),
                result.createdAt(),
                result.readAt()
        );
    }
}
