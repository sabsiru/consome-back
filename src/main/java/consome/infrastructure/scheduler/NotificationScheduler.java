package consome.infrastructure.scheduler;

import consome.domain.notification.repository.NotificationRepository;
import consome.infrastructure.notification.SseEmitterRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationScheduler {

    private final SseEmitterRepository sseEmitterRepository;
    private final NotificationRepository notificationRepository;

    @Scheduled(fixedRate = 30000) // 30초
    public void sendHeartbeat() {
        sseEmitterRepository.sendHeartbeat();
    }

    @Scheduled(cron = "0 0 3 * * *") // 매일 새벽 3시
    @Transactional
    public void cleanupOldNotifications() {
        LocalDateTime now = LocalDateTime.now();
        int deletedRead = notificationRepository.deleteReadBefore(now.minusDays(30));
        int deletedUnread = notificationRepository.deleteUnreadBefore(now.minusDays(90));
        log.info("알림 정리 완료: 읽은 알림 {}개, 안읽은 알림 {}개 삭제", deletedRead, deletedUnread);
    }
}
