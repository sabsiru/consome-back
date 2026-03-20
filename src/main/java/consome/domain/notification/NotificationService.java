package consome.domain.notification;

import consome.domain.notification.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;

    @Transactional
    public Notification create(Long userId, NotificationType type, Long actorId,
                                Long targetId, Long relatedId, Long referenceId, String message) {
        Notification notification = Notification.create(userId, type, actorId, targetId, relatedId, referenceId, message);
        return notificationRepository.save(notification);
    }

    @Transactional(readOnly = true)
    public long countUnread(Long userId) {
        return notificationRepository.countUnreadByUserId(userId);
    }

    @Transactional
    public Notification markAsRead(Long notificationId, Long userId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new IllegalArgumentException("알림을 찾을 수 없습니다: " + notificationId));
        if (!notification.getUserId().equals(userId)) {
            throw new IllegalArgumentException("본인의 알림만 읽음 처리할 수 있습니다.");
        }
        notification.markAsRead();
        return notification;
    }

    @Transactional
    public int markAllAsRead(Long userId) {
        return notificationRepository.markAllAsRead(userId);
    }

    @Transactional
    public void delete(Long notificationId, Long userId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new IllegalArgumentException("알림을 찾을 수 없습니다: " + notificationId));
        if (!notification.getUserId().equals(userId)) {
            throw new IllegalArgumentException("본인의 알림만 삭제할 수 있습니다.");
        }
        notificationRepository.delete(notification);
    }

    @Transactional
    public int deleteAll(Long userId) {
        return notificationRepository.deleteAllByUserId(userId);
    }
}
