package consome.infrastructure.jwt;

import consome.domain.user.Role;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.Map;

@Component
public class JwtProvider {

    private final JwtProperties properties;
    private final Key key;

    public JwtProvider(JwtProperties properties) {
        this.properties = properties;
        this.key = Keys.hmacShaKeyFor(properties.getSecret().getBytes());
    }

    /** ✅ AccessToken 생성 */
    public String createAccessToken(Long userId, Role role) {
        return createToken(userId, role, properties.getAccessTokenExpiration());
    }

    /** ✅ RefreshToken 생성 */
    public String createRefreshToken(Long userId, Role role) {
        return createToken(userId, role, properties.getRefreshTokenExpiration());
    }

    /** ✅ JWT 생성 공통 로직 */
    private String createToken(Long userId, Role role, long expiration) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + expiration);

        return Jwts.builder()
                .setSubject(String.valueOf(userId))
                .addClaims(Map.of("role", role.name()))
                .setIssuedAt(now)
                .setExpiration(expiry)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    /** ✅ JWT 파싱 및 유효성 검증 */
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    /** ✅ 토큰에서 userId 추출 */
    public Long getUserId(String token) {
        Claims claims = parseClaims(token);
        return Long.parseLong(claims.getSubject());
    }

    /** ✅ 토큰에서 role 추출 */
    public Role getRole(String token) {
        Claims claims = parseClaims(token);
        String roleName = claims.get("role", String.class);
        return Role.valueOf(roleName); // String → Enum 변환
    }

    private Claims parseClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}