package consome.infrastructure.redis;

import consome.domain.password.PasswordResetTokenRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Slf4j
@Repository
public class PasswordResetRedisRepository implements PasswordResetTokenRepository {

    private static final String TOKEN_KEY_PREFIX = "password:reset:";
    private static final String COOLDOWN_KEY_PREFIX = "password:cooldown:";
    private static final long TOKEN_TTL_HOURS = 1;
    private static final long COOLDOWN_TTL_SECONDS = 60;

    private final RedisTemplate<String, String> redisTemplate;

    @Autowired(required = false)
    public PasswordResetRedisRepository(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void saveToken(String token, Long userId) {
        try {
            String key = TOKEN_KEY_PREFIX + token;
            redisTemplate.opsForValue().set(key, String.valueOf(userId), TOKEN_TTL_HOURS, TimeUnit.HOURS);
        } catch (Exception e) {
            log.warn("Redis operation failed: {}", e.getMessage());
        }
    }

    public Optional<Long> findUserIdByToken(String token) {
        try {
            String key = TOKEN_KEY_PREFIX + token;
            String value = redisTemplate.opsForValue().get(key);
            if (value == null) {
                return Optional.empty();
            }
            return Optional.of(Long.parseLong(value));
        } catch (Exception e) {
            log.warn("Redis operation failed: {}", e.getMessage());
            return Optional.empty();
        }
    }

    public void deleteToken(String token) {
        try {
            String key = TOKEN_KEY_PREFIX + token;
            redisTemplate.delete(key);
        } catch (Exception e) {
            log.warn("Redis operation failed: {}", e.getMessage());
        }
    }

    public void saveCooldown(String email) {
        try {
            String key = COOLDOWN_KEY_PREFIX + email;
            redisTemplate.opsForValue().set(key, "1", COOLDOWN_TTL_SECONDS, TimeUnit.SECONDS);
        } catch (Exception e) {
            log.warn("Redis operation failed: {}", e.getMessage());
        }
    }

    public boolean isCooldownActive(String email) {
        try {
            String key = COOLDOWN_KEY_PREFIX + email;
            return Boolean.TRUE.equals(redisTemplate.hasKey(key));
        } catch (Exception e) {
            log.warn("Redis operation failed: {}", e.getMessage());
            return false;
        }
    }
}
