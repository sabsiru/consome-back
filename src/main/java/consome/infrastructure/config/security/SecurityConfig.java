package consome.infrastructure.config.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())               // 일단 CSRF 비활성화 (API 서버)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/", "/api/**").permitAll() // 전부 허용
                        .anyRequest().permitAll()
                )
                .formLogin(login -> login.disable())        // 폼 로그인 비활성화
                .httpBasic(basic -> basic.disable());       // HTTP Basic 비활성화

        return http.build();
    }
}
