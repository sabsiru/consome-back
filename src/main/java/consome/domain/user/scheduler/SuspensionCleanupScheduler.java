package consome.domain.user.scheduler;

import consome.domain.user.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class SuspensionCleanupScheduler {

    private final UserService userService;

    @Scheduled(cron = "0 0 * * * *")
    public void cleanupExpiredSuspensions() {
        log.info("만료 제재 정리 시작");
        int cleanedCount = userService.cleanupExpiredSuspensions();
        if (cleanedCount > 0) {
            log.info("만료 제재 정리 완료 - {}명 해제", cleanedCount);
        }
    }
}
