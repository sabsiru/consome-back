package consome.infrastructure.redis;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Slf4j
@Repository
public class SseTokenRedisRepository {

    private static final String KEY_PREFIX = "sse_token:";
    private static final long TTL_SECONDS = 30;

    private final RedisTemplate<String, String> redisTemplate;

    @Autowired(required = false)
    public SseTokenRedisRepository(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public String createToken(Long userId) {
        String token = UUID.randomUUID().toString().replace("-", "");
        String key = KEY_PREFIX + token;
        try {
            redisTemplate.opsForValue().set(key, userId.toString(), TTL_SECONDS, TimeUnit.SECONDS);
        } catch (Exception e) {
            log.warn("SSE 토큰 저장 실패: {}", e.getMessage());
            throw new RuntimeException("SSE 토큰 발급에 실패했습니다.");
        }
        return token;
    }

    public Optional<Long> consumeToken(String token) {
        String key = KEY_PREFIX + token;
        try {
            String userId = redisTemplate.opsForValue().getAndDelete(key);
            return Optional.ofNullable(userId).map(Long::parseLong);
        } catch (Exception e) {
            log.warn("SSE 토큰 조회 실패: {}", e.getMessage());
            return Optional.empty();
        }
    }
}
