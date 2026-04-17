package consome.interfaces.auth.v1;

import consome.application.auth.AuthFacade;
import consome.application.auth.TokenRefreshResult;
import consome.infrastructure.aop.RateLimit;
import consome.infrastructure.security.CustomUserDetails;
import consome.interfaces.auth.dto.TokenRefreshRequest;
import consome.interfaces.auth.dto.TokenRefreshResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthV1Controller {

    private final AuthFacade authFacade;

    @PostMapping("/refresh")
    @RateLimit(key = "refresh", limit = 10, byIp = true)
    public ResponseEntity<TokenRefreshResponse> refresh(@RequestBody @Valid TokenRefreshRequest request) {
        TokenRefreshResult result = authFacade.refresh(request.refreshToken());
        return ResponseEntity.ok(TokenRefreshResponse.from(result));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@AuthenticationPrincipal CustomUserDetails userDetails,
                                       HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ") || authHeader.length() <= 7) {
            return ResponseEntity.badRequest().build();
        }
        String accessToken = authHeader.substring(7);
        authFacade.logout(userDetails.getUserId(), accessToken);
        return ResponseEntity.noContent().build();
    }
}
