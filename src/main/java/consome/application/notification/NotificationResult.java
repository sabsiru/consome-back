package consome.application.notification;

import consome.domain.notification.Notification;
import consome.domain.notification.NotificationType;

import java.time.LocalDateTime;

public record NotificationResult(
        Long id,
        Long userId,
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
    public static NotificationResult from(Notification n, String actorNickname) {
        return new NotificationResult(
                n.getId(),
                n.getUserId(),
                n.getType(),
                n.getActorId(),
                actorNickname,
                n.getTargetId(),
                n.getRelatedId(),
                n.getReferenceId(),
                n.getMessage(),
                n.isRead(),
                n.getCreatedAt(),
                n.getReadAt()
        );
    }
}
