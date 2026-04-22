package consome.infrastructure.redis;

import consome.domain.statistics.ActivityType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
@Repository
@RequiredArgsConstructor
public class HourlyActivityRedisRepository {

    private static final String KEY_PREFIX = "activity:";
    private static final ZoneId KST = ZoneId.of("Asia/Seoul");
    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("yyyyMMdd");
    private static final long TTL_SECONDS = 172800; // 48시간 안전 마진

    private final RedisTemplate<String, String> redisTemplate;

    /**
     * activity:{type}:{yyyymmdd}:{hour} INCR
     */
    public void increment(ActivityType type) {
        LocalDateTime now = LocalDateTime.now(KST);
        String key = buildKey(type, now.toLocalDate(), now.getHour());
        try {
            redisTemplate.opsForValue().increment(key);
            redisTemplate.expire(key, TTL_SECONDS, TimeUnit.SECONDS);
        } catch (Exception e) {
            log.warn("activity increment failed ({}): {}", key, e.getMessage());
        }
    }

    /**
     * 특정 날짜/시간/타입 카운트 조회
     */
    public long getCount(ActivityType type, LocalDate date, int hour) {
        String key = buildKey(type, date, hour);
        try {
            String value = redisTemplate.opsForValue().get(key);
            return value == null ? 0L : Long.parseLong(value);
        } catch (Exception e) {
            log.warn("activity get failed ({}): {}", key, e.getMessage());
            return 0L;
        }
    }

    /**
     * 스케줄러 flush 용 — 모든 activity:* 키 스캔
     */
    public List<FlushEntry> scanAll() {
        List<FlushEntry> entries = new ArrayList<>();
        try {
            ScanOptions options = ScanOptions.scanOptions().match(KEY_PREFIX + "*").count(500).build();
            try (Cursor<String> cursor = redisTemplate.scan(options)) {
                while (cursor.hasNext()) {
                    String key = cursor.next();
                    FlushEntry entry = parseKey(key);
                    if (entry == null) continue;
                    String value = redisTemplate.opsForValue().get(key);
                    if (value == null) continue;
                    long count = Long.parseLong(value);
                    entries.add(new FlushEntry(entry.type(), entry.date(), entry.hour(), count));
                }
            }
        } catch (Exception e) {
            log.warn("activity scanAll failed: {}", e.getMessage());
        }
        return entries;
    }

    /**
     * flush 완료 후 해당 키의 카운트만큼 차감 (누락 방지 — flush 중 추가 증분 보존)
     */
    public void decrement(ActivityType type, LocalDate date, int hour, long delta) {
        String key = buildKey(type, date, hour);
        try {
            redisTemplate.opsForValue().decrement(key, delta);
        } catch (Exception e) {
            log.warn("activity decrement failed ({}): {}", key, e.getMessage());
        }
    }

    private String buildKey(ActivityType type, LocalDate date, int hour) {
        return KEY_PREFIX + type.name() + ":" + date.format(DATE_FMT) + ":" + hour;
    }

    private FlushEntry parseKey(String key) {
        // activity:{TYPE}:{yyyymmdd}:{hour}
        String[] parts = key.split(":");
        if (parts.length != 4) return null;
        try {
            ActivityType type = ActivityType.valueOf(parts[1]);
            LocalDate date = LocalDate.parse(parts[2], DATE_FMT);
            int hour = Integer.parseInt(parts[3]);
            return new FlushEntry(type, date, hour, 0L);
        } catch (Exception e) {
            return null;
        }
    }

    public record FlushEntry(ActivityType type, LocalDate date, int hour, long count) {}
}
