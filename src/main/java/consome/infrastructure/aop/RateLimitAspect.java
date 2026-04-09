package consome.infrastructure.aop;

import consome.domain.common.exception.RateLimitException;
import consome.infrastructure.redis.RateLimitRedisRepository;
import consome.infrastructure.security.CustomUserDetails;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.Duration;

@Aspect
@Component
@RequiredArgsConstructor
public class RateLimitAspect {

    private final RateLimitRedisRepository rateLimitRedisRepository;

    @Before("@annotation(rateLimit)")
    public void checkRateLimit(RateLimit rateLimit) {
        String identifier = rateLimit.byIp() ? resolveIp() : resolveUserId();
        String key = rateLimit.key() + ":" + identifier;
        Duration window = Duration.of(rateLimit.window(), rateLimit.timeUnit().toChronoUnit());

        if (!rateLimitRedisRepository.isAllowed(key, rateLimit.limit(), window)) {
            throw new RateLimitException();
        }
    }

    private String resolveIp() {
        ServletRequestAttributes attrs =
                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attrs == null) {
            return "unknown";
        }
        HttpServletRequest request = attrs.getRequest();
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }

    private String resolveUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof CustomUserDetails userDetails) {
            return String.valueOf(userDetails.getUserId());
        }
        // 인증 안 된 경우 IP로 fallback
        return resolveIp();
    }
}
