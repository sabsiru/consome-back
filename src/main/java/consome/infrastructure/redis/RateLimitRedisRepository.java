package consome.infrastructure.redis;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;

@Slf4j
@Repository
@RequiredArgsConstructor
public class RateLimitRedisRepository {

    private static final String PREFIX = "ratelimit:";

    private final RedisTemplate<String, String> redisTemplate;

    /**
     * Fixed window counter 방식으로 요청 횟수 체크 및 증가
     *
     * @return true if request is allowed, false if rate limit exceeded
     */
    public boolean isAllowed(String key, int limit, Duration window) {
        String redisKey = PREFIX + key;
        try {
            Long count = redisTemplate.opsForValue().increment(redisKey);
            if (count == null) {
                return true;
            }
            if (count == 1) {
                redisTemplate.expire(redisKey, window);
            }
            return count <= limit;
        } catch (Exception e) {
            log.error("Rate limit Redis failure - fail-open activated. key={}, error={}", key, e.getMessage(), e);
            return true; // Redis 장애 시 요청 허용 (fail-open)
        }
    }
}
