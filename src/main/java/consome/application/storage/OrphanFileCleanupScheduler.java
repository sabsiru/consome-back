package consome.application.storage;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrphanFileCleanupScheduler {

    private final OrphanFileCleanupService cleanupService;

    @Scheduled(cron = "${cleanup.orphan-files.cron:0 0 3 * * *}")
    public void scheduleCleanup() {
        log.info("고아 파일 정리 배치 시작");
        OrphanFileCleanupResult result = cleanupService.cleanup();
        log.info("고아 파일 정리 완료 - 전체: {}, 참조: {}, 삭제: {}",
            result.totalFiles(), result.referencedFiles(), result.deletedFiles());
    }
}
