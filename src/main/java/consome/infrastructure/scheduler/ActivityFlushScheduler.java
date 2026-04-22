package consome.infrastructure.scheduler;

import consome.domain.statistics.entity.HourlyActivity;
import consome.domain.statistics.repository.HourlyActivityRepository;
import consome.infrastructure.redis.HourlyActivityRedisRepository;
import consome.infrastructure.redis.HourlyActivityRedisRepository.FlushEntry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class ActivityFlushScheduler {

    private final HourlyActivityRedisRepository hourlyActivityRedisRepository;
    private final HourlyActivityRepository hourlyActivityRepository;

    /**
     * 매시간 정각 5분 후 — Redis 활동 카운트를 DB로 flush
     * (정각 직후 발생한 활동까지 포함)
     */
    @Scheduled(cron = "0 5 * * * *")
    @Transactional
    public void flush() {
        List<FlushEntry> entries = hourlyActivityRedisRepository.scanAll();
        if (entries.isEmpty()) {
            log.debug("hourly activity flush: no entries");
            return;
        }

        long total = 0;
        for (FlushEntry entry : entries) {
            if (entry.count() <= 0) continue;

            HourlyActivity row = hourlyActivityRepository
                    .findByActivityDateAndHourAndType(entry.date(), entry.hour(), entry.type())
                    .orElseGet(() -> hourlyActivityRepository.save(
                            HourlyActivity.of(entry.date(), entry.hour(), entry.type(), 0L)));
            row.addCount(entry.count());

            // flush한 만큼만 차감 (flush 동안 추가 증분 보존)
            hourlyActivityRedisRepository.decrement(entry.type(), entry.date(), entry.hour(), entry.count());
            total += entry.count();
        }

        log.info("hourly activity flush: entries={}, total={}", entries.size(), total);
    }
}
