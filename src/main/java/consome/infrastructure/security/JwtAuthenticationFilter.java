package consome.infrastructure.security;

import consome.domain.user.Role;
import consome.infrastructure.jwt.JwtProvider;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtProvider jwtProvider;

    public JwtAuthenticationFilter(JwtProvider jwtProvider) {
        this.jwtProvider = jwtProvider;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        log.info("JwtAuthenticationFilter activated for URI: {}", request.getRequestURI());
        String authHeader = request.getHeader("Authorization");

        // Authorization 헤더 확인
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);

            if (jwtProvider.validateToken(token)) {
                Long userId = jwtProvider.getUserId(token);
                Role role = jwtProvider.getRole(token);

                CustomUserDetails customUserDetails = new CustomUserDetails(userId, role);

                // 인증 객체 생성
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(customUserDetails, null, customUserDetails.getAuthorities());
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                // SecurityContext에 등록
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }

        filterChain.doFilter(request, response);
    }
}