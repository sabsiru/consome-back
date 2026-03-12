package consome.infrastructure.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import consome.domain.user.Role;
import consome.domain.user.User;
import consome.domain.user.repository.UserRepository;
import consome.infrastructure.jwt.JwtProvider;
import consome.infrastructure.redis.TokenRedisRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;

@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtProvider jwtProvider;
    private final TokenRedisRepository tokenRedisRepository;
    private final UserRepository userRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public JwtAuthenticationFilter(JwtProvider jwtProvider, TokenRedisRepository tokenRedisRepository,
                                   UserRepository userRepository) {
        this.jwtProvider = jwtProvider;
        this.tokenRedisRepository = tokenRedisRepository;
        this.userRepository = userRepository;
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

                Optional<User> userOpt = userRepository.findById(userId);
                if (userOpt.isPresent() && userOpt.get().isSuspended()) {
                    User user = userOpt.get();
                    response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                    response.setCharacterEncoding("UTF-8");

                    String code;
                    String message;
                    if (user.isPermanentlyBanned()) {
                        code = "USER_BANNED";
                        message = "계정이 영구 정지되었습니다. 사유: " + user.getSuspendReason();
                    } else {
                        code = "USER_SUSPENDED";
                        String until = user.getSuspendedUntil().toString().replace("T", " ").substring(0, 16);
                        message = "계정이 정지되었습니다. 사유: " + user.getSuspendReason() + " (해제: " + until + ")";
                    }

                    response.getWriter().write(objectMapper.writeValueAsString(Map.of("code", code, "message", message)));
                    return;
                }

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
