package consome.infrastructure.scheduler;

import consome.infrastructure.redis.SearchKeywordRedisRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class SearchKeywordResetScheduler {

    private final SearchKeywordRedisRepository searchKeywordRedisRepository;

    /**
     * 매일 00:00 KST — 일간 인기 검색어 ZSET 초기화
     */
    @Scheduled(cron = "0 0 0 * * *", zone = "Asia/Seoul")
    public void resetDailyKeywords() {
        searchKeywordRedisRepository.resetDay();
        log.info("daily search keyword ZSET reset");
    }
}
