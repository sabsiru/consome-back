package consome.config;

import org.mockito.invocation.InvocationOnMock;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cache.CacheManager;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;

import java.util.concurrent.ConcurrentHashMap;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@Configuration
@ConditionalOnProperty(name = "spring.cache.type", havingValue = "none")
public class TestRedisConfig {

    private final ConcurrentHashMap<String, Double> zsetStore = new ConcurrentHashMap<>();

    @Bean
    @Primary
    public RedisConnectionFactory redisConnectionFactory() {
        return mock(RedisConnectionFactory.class);
    }

    @Bean
    @Primary
    @SuppressWarnings("unchecked")
    public RedisTemplate<String, String> redisTemplate() {
        RedisTemplate<String, String> template = mock(RedisTemplate.class);
        ZSetOperations<String, String> zSetOps = mock(ZSetOperations.class);

        when(template.opsForZSet()).thenReturn(zSetOps);

        // add: 점수 저장
        when(zSetOps.add(any(), any(), anyDouble())).thenAnswer((InvocationOnMock inv) -> {
            String member = inv.getArgument(1);
            Double score = inv.getArgument(2);
            zsetStore.put(member, score);
            return true;
        });

        // score: 점수 조회
        when(zSetOps.score(any(), any())).thenAnswer((InvocationOnMock inv) -> {
            String member = inv.getArgument(1);
            return zsetStore.get(member);
        });

        // remove: 삭제
        doAnswer((InvocationOnMock inv) -> {
            Object[] members = inv.getArguments();
            for (int i = 1; i < members.length; i++) {
                zsetStore.remove(members[i].toString());
            }
            return (long)(members.length - 1);
        }).when(zSetOps).remove(any(), any());

        when(template.expire(any(), anyLong(), any())).thenReturn(true);

        return template;
    }

    @Bean
    @Primary
    public CacheManager cacheManager() {
        return new ConcurrentMapCacheManager();
    }
}
