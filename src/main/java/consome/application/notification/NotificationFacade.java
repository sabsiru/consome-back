package consome.application.notification;

import consome.domain.notification.Notification;
import consome.domain.notification.NotificationService;
import consome.domain.notification.NotificationType;
import consome.domain.notification.repository.NotificationQueryRepository;
import consome.domain.user.UserService;
import consome.infrastructure.jwt.JwtProvider;
import consome.infrastructure.notification.SseEmitterRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationFacade {

    private final NotificationService notificationService;
    private final NotificationQueryRepository notificationQueryRepository;
    private final UserService userService;
    private final SseEmitterRepository sseEmitterRepository;
    private final JwtProvider jwtProvider;

    public void notify(Long userId, NotificationType type, Long actorId,
                       Long targetId, Long relatedId, Long referenceId, String message) {
        // 자기 자신에게는 알림 안 보냄
        if (userId.equals(actorId)) {
            return;
        }

        Notification notification = notificationService.create(userId, type, actorId, targetId, relatedId, referenceId, message);
        String actorNickname = resolveNickname(actorId);
        NotificationResult result = NotificationResult.from(notification, actorNickname);

        // 트랜잭션 커밋 후 SSE 전송 — I/O가 DB 커넥션을 점유하지 않음
        if (TransactionSynchronizationManager.isSynchronizationActive()) {
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override
                public void afterCommit() {
                    sseEmitterRepository.send(userId, result);
                }
            });
        } else {
            sseEmitterRepository.send(userId, result);
        }
    }

    @Transactional(readOnly = true)
    public Page<NotificationResult> getNotifications(Long userId, Pageable pageable) {
        return notificationQueryRepository.findByUserId(userId, pageable);
    }

    @Transactional(readOnly = true)
    public long getUnreadCount(Long userId) {
        return notificationService.countUnread(userId);
    }

    @Transactional
    public NotificationResult markAsRead(Long notificationId, Long userId) {
        Notification notification = notificationService.markAsRead(notificationId, userId);
        String actorNickname = resolveNickname(notification.getActorId());
        return NotificationResult.from(notification, actorNickname);
    }

    @Transactional
    public void markAllAsRead(Long userId) {
        notificationService.markAllAsRead(userId);
    }

    @Transactional
    public void delete(Long notificationId, Long userId) {
        notificationService.delete(notificationId, userId);
    }

    @Transactional
    public void deleteAll(Long userId) {
        notificationService.deleteAll(userId);
    }

    private String resolveNickname(Long userId) {
        try {
            return userService.getNicknameById(userId);
        } catch (Exception e) {
            return "알 수 없는 사용자";
        }
    }

    public SseEmitter subscribe(String token) {
        if (!jwtProvider.validateToken(token)) {
            throw new IllegalArgumentException("유효하지 않은 토큰입니다.");
        }
        Long userId = jwtProvider.getUserId(token);
        return sseEmitterRepository.subscribe(userId);
    }
}
