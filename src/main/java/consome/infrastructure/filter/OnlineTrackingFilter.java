package consome.infrastructure.filter;

import consome.domain.statistics.entity.SiteVisit;
import consome.domain.statistics.repository.SiteVisitRepository;
import consome.infrastructure.redis.OnlineUserRedisRepository;
import consome.infrastructure.security.CustomUserDetails;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.LocalDate;

@Slf4j
@Component
@RequiredArgsConstructor
public class OnlineTrackingFilter extends OncePerRequestFilter {

    private final OnlineUserRedisRepository onlineUserRedisRepository;
    private final SiteVisitRepository siteVisitRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        try {
            trackActivity(request);
        } catch (Exception e) {
            log.warn("Failed to track user activity: {}", e.getMessage());
        }

        filterChain.doFilter(request, response);
    }

    private void trackActivity(HttpServletRequest request) {
        Long userId = extractUserId();
        String ip = extractClientIp(request);

        // Redis: 온라인 상태 기록
        String memberKey = OnlineUserRedisRepository.buildMemberKey(userId, ip);
        onlineUserRedisRepository.recordActivity(memberKey);

        // DB: 일일 방문자 기록 (중복 방지)
        String visitorKey = SiteVisit.buildVisitorKey(userId, ip);
        LocalDate today = LocalDate.now();
        if (!siteVisitRepository.existsByVisitorKeyAndVisitDate(visitorKey, today)) {
            siteVisitRepository.save(SiteVisit.create(visitorKey));
        }
    }

    private Long extractUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof CustomUserDetails userDetails) {
            return userDetails.getUserId();
        }
        return null;
    }

    private String extractClientIp(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        // 정적 리소스 및 헬스체크 제외
        return path.startsWith("/actuator") ||
               path.startsWith("/favicon") ||
               path.endsWith(".js") ||
               path.endsWith(".css") ||
               path.endsWith(".png") ||
               path.endsWith(".jpg");
    }
}
