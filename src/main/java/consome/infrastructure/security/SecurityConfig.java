package consome.infrastructure.security;

import consome.domain.user.repository.UserRepository;
import consome.infrastructure.filter.OnlineTrackingFilter;
import consome.infrastructure.jwt.JwtProvider;
import consome.infrastructure.redis.TokenRedisRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.http.HttpMethod;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@Order(0)
public class SecurityConfig {

    private final JwtProvider jwtProvider;
    private final TokenRedisRepository tokenRedisRepository;
    private final UserRepository userRepository;
    private final OnlineTrackingFilter onlineTrackingFilter;

    @Value("${app.cors.allowed-origins}")
    private String allowedOrigins;

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() {
        return new JwtAuthenticationFilter(jwtProvider, tokenRedisRepository, userRepository);
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, JwtAuthenticationFilter jwtAuthenticationFilter) throws Exception {
        http
                .headers(headers -> headers
                        .frameOptions(frame -> frame.deny())
                        .contentTypeOptions(content -> {})
                        .cacheControl(cache -> {})
                )
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                        // Auth
                        .requestMatchers("/api/v1/auth/logout").authenticated()

                        // Post write operations
                        .requestMatchers(HttpMethod.POST, "/api/v1/posts", "/api/v1/posts/images", "/api/v1/posts/videos").authenticated()
                        .requestMatchers(HttpMethod.PUT, "/api/v1/posts/*").authenticated()
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/posts/*").authenticated()
                        .requestMatchers(HttpMethod.POST, "/api/v1/posts/*/like", "/api/v1/posts/*/dislike").authenticated()

                        // Comment write operations
                        .requestMatchers(HttpMethod.POST, "/api/v1/posts/*/comments").authenticated()
                        .requestMatchers(HttpMethod.PUT, "/api/v1/posts/*/comments/*").authenticated()
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/posts/*/comments/*").authenticated()
                        .requestMatchers(HttpMethod.POST, "/api/v1/posts/*/comments/*/like", "/api/v1/posts/*/comments/*/dislike").authenticated()

                        // Messages - all require auth
                        .requestMatchers("/api/v1/messages/**").authenticated()

                        // Notifications - subscribe uses token param auth
                        .requestMatchers(HttpMethod.GET, "/api/v1/notifications/subscribe").permitAll()
                        .requestMatchers("/api/v1/notifications/**").authenticated()

                        // Reports
                        .requestMatchers("/api/v1/reports/**").authenticated()

                        // Board favorites
                        .requestMatchers("/api/v1/boards/favorites").authenticated()

                        // Admin
                        .requestMatchers("/api/v1/admin/**").hasAnyRole("ADMIN", "MANAGER")

                        .anyRequest().permitAll()
                )
                .formLogin(form -> form.disable())
                .httpBasic(basic -> basic.disable())
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterAfter(onlineTrackingFilter, JwtAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(org.springframework.web.servlet.config.annotation.CorsRegistry registry) {
                registry.addMapping("/**")
                        .allowedOrigins(allowedOrigins.split(","))
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH")
                        .allowCredentials(true);
            }
        };
    }
}