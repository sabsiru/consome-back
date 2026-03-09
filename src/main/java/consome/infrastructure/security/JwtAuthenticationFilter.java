package consome.infrastructure.security;

import consome.domain.user.Role;
import consome.infrastructure.jwt.JwtProvider;
import consome.infrastructure.redis.TokenRedisRepository;
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
    private final TokenRedisRepository tokenRedisRepository;

    public JwtAuthenticationFilter(JwtProvider jwtProvider, TokenRedisRepository tokenRedisRepository) {
        this.jwtProvider = jwtProvider;
        this.tokenRedisRepository = tokenRedisRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);

            if (jwtProvider.validateToken(token)) {
                String jti = jwtProvider.getJti(token);
                if (tokenRedisRepository.isBlacklisted(jti)) {
                    filterChain.doFilter(request, response);
                    return;
                }

                Long userId = jwtProvider.getUserId(token);
                Role role = jwtProvider.getRole(token);

                CustomUserDetails customUserDetails = new CustomUserDetails(userId, role);

                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(customUserDetails, null, customUserDetails.getAuthorities());
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }

        filterChain.doFilter(request, response);
    }
}
