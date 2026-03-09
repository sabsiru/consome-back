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
import org.springframework.data.redis.core.ValueOperations;
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
    private final ConcurrentHashMap<String, String> valueStore = new ConcurrentHashMap<>();

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
        ValueOperations<String, String> valueOps = mock(ValueOperations.class);

        when(template.opsForZSet()).thenReturn(zSetOps);
        when(template.opsForValue()).thenReturn(valueOps);

        // === ZSet Operations ===
        when(zSetOps.add(any(), any(), anyDouble())).thenAnswer((InvocationOnMock inv) -> {
            String member = inv.getArgument(1);
            Double score = inv.getArgument(2);
            zsetStore.put(member, score);
            return true;
        });

        when(zSetOps.score(any(), any())).thenAnswer((InvocationOnMock inv) -> {
            String member = inv.getArgument(1);
            return zsetStore.get(member);
        });

        doAnswer((InvocationOnMock inv) -> {
            Object[] members = inv.getArguments();
            for (int i = 1; i < members.length; i++) {
                zsetStore.remove(members[i].toString());
            }
            return (long)(members.length - 1);
        }).when(zSetOps).remove(any(), any());

        // === Value Operations ===
        // set(key, value, timeout, unit)
        doAnswer(inv -> {
            String key = inv.getArgument(0);
            String value = inv.getArgument(1);
            valueStore.put(key, value);
            return null;
        }).when(valueOps).set(any(), any(), anyLong(), any());

        // set(key, value)
        doAnswer(inv -> {
            String key = inv.getArgument(0);
            String value = inv.getArgument(1);
            valueStore.put(key, value);
            return null;
        }).when(valueOps).set(any(), any());

        // get(key)
        when(valueOps.get(any())).thenAnswer(inv -> {
            String key = inv.getArgument(0);
            return valueStore.get(key);
        });

        // === Template Operations ===
        when(template.hasKey(any())).thenAnswer(inv -> {
            String key = inv.getArgument(0);
            return valueStore.containsKey(key);
        });

        when(template.delete(any(String.class))).thenAnswer(inv -> {
            String key = inv.getArgument(0);
            return valueStore.remove(key) != null;
        });

        when(template.expire(any(), anyLong(), any())).thenReturn(true);

        return template;
    }

    @Bean
    @Primary
    public CacheManager cacheManager() {
        return new ConcurrentMapCacheManager();
    }
}
