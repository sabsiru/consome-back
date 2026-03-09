package consome.infrastructure.redis;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Slf4j
@Repository
public class TokenRedisRepository {

    private static final String REFRESH_KEY_PREFIX = "refresh:";
    private static final String BLACKLIST_KEY_PREFIX = "blacklist:";

    private final RedisTemplate<String, String> redisTemplate;

    @Autowired(required = false)
    public TokenRedisRepository(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void saveRefreshToken(Long userId, String refreshToken, long ttlMillis) {
        try {
            String key = REFRESH_KEY_PREFIX + userId;
            redisTemplate.opsForValue().set(key, refreshToken, ttlMillis, TimeUnit.MILLISECONDS);
        } catch (Exception e) {
            log.warn("Redis operation failed: {}", e.getMessage());
        }
    }

    public Optional<String> findRefreshToken(Long userId) {
        try {
            String key = REFRESH_KEY_PREFIX + userId;
            String value = redisTemplate.opsForValue().get(key);
            return Optional.ofNullable(value);
        } catch (Exception e) {
            log.warn("Redis operation failed: {}", e.getMessage());
            return Optional.empty();
        }
    }

    public void deleteRefreshToken(Long userId) {
        try {
            String key = REFRESH_KEY_PREFIX + userId;
            redisTemplate.delete(key);
        } catch (Exception e) {
            log.warn("Redis operation failed: {}", e.getMessage());
        }
    }

    public void addToBlacklist(String jti, long remainingTtlMillis) {
        try {
            String key = BLACKLIST_KEY_PREFIX + jti;
            redisTemplate.opsForValue().set(key, "1", remainingTtlMillis, TimeUnit.MILLISECONDS);
        } catch (Exception e) {
            log.warn("Redis operation failed: {}", e.getMessage());
        }
    }

    public boolean isBlacklisted(String jti) {
        try {
            String key = BLACKLIST_KEY_PREFIX + jti;
            return Boolean.TRUE.equals(redisTemplate.hasKey(key));
        } catch (Exception e) {
            log.warn("Redis operation failed: {}", e.getMessage());
            return false;
        }
    }
}
