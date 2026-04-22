package consome.infrastructure.redis;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Slf4j
@Repository
@RequiredArgsConstructor
public class SearchKeywordRedisRepository {

    private static final String HOUR_KEY = "search:rank:hour";
    private static final String DAY_KEY = "search:rank:day";
    private static final String DEDUPE_PREFIX = "search:dedupe:";
    private static final long DEDUPE_TTL_SECONDS = 60;
    private static final long HOUR_TTL_SECONDS = 7200;    // 2시간 안전 마진
    private static final long DAY_TTL_SECONDS = 172800;   // 48시간 안전 마진

    private final RedisTemplate<String, String> redisTemplate;

    /**
     * 중복 카운트 방지: 60초 이내 같은 (requester, keyword) 조합은 카운트 skip
     * @return true = 신규(카운트 진행), false = 중복(skip)
     */
    public boolean markIfAbsent(String requesterKey, String normalizedKeyword) {
        try {
            String key = DEDUPE_PREFIX + requesterKey + ":" + normalizedKeyword;
            Boolean set = redisTemplate.opsForValue()
                    .setIfAbsent(key, "1", DEDUPE_TTL_SECONDS, TimeUnit.SECONDS);
            return Boolean.TRUE.equals(set);
        } catch (Exception e) {
            log.warn("dedupe check failed: {}", e.getMessage());
            return false;
        }
    }

    public void increment(String normalizedKeyword) {
        try {
            redisTemplate.opsForZSet().incrementScore(HOUR_KEY, normalizedKeyword, 1.0);
            redisTemplate.expire(HOUR_KEY, HOUR_TTL_SECONDS, TimeUnit.SECONDS);

            redisTemplate.opsForZSet().incrementScore(DAY_KEY, normalizedKeyword, 1.0);
            redisTemplate.expire(DAY_KEY, DAY_TTL_SECONDS, TimeUnit.SECONDS);
        } catch (Exception e) {
            log.warn("keyword increment failed: {}", e.getMessage());
        }
    }

    /**
     * TOP N 조회
     * @param period "hour" or "day"
     */
    public List<KeywordScore> getTopKeywords(String period, int limit) {
        try {
            String key = "hour".equals(period) ? HOUR_KEY : DAY_KEY;
            Set<ZSetOperations.TypedTuple<String>> tuples =
                    redisTemplate.opsForZSet().reverseRangeWithScores(key, 0, limit - 1);
            if (tuples == null || tuples.isEmpty()) return Collections.emptyList();

            return tuples.stream()
                    .map(t -> new KeywordScore(t.getValue(), t.getScore() == null ? 0L : t.getScore().longValue()))
                    .toList();
        } catch (Exception e) {
            log.warn("getTopKeywords failed: {}", e.getMessage());
            return Collections.emptyList();
        }
    }

    /**
     * 일간 ZSET 초기화 (매일 00:00 KST 스케줄러에서 호출)
     */
    public void resetDay() {
        try {
            redisTemplate.delete(DAY_KEY);
        } catch (Exception e) {
            log.warn("resetDay failed: {}", e.getMessage());
        }
    }

    public record KeywordScore(String keyword, long count) {}
}
