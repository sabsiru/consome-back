package consome.infrastructure.redis;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.concurrent.TimeUnit;

@Repository
@RequiredArgsConstructor
public class PopularPostRedisRepository {

    private static final String KEY = "popular_post_candidates";
    private static final long TTL_DAYS = 5;

    private final RedisTemplate<String, String> redisTemplate;

    public void addCandidate(Long postId, double score) {
        redisTemplate.opsForZSet().add(KEY, postId.toString(), score);
        redisTemplate.expire(KEY, TTL_DAYS, TimeUnit.DAYS);
    }

    public Double getScore(Long postId) {
        return redisTemplate.opsForZSet().score(KEY, postId.toString());
    }

    public void removeCandidate(Long postId) {
        redisTemplate.opsForZSet().remove(KEY, postId.toString());
    }

    public boolean exists(Long postId) {
        return getScore(postId) != null;
    }
}
