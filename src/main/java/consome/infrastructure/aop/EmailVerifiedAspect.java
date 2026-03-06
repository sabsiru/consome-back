package consome.infrastructure.aop;

import consome.domain.user.User;
import consome.domain.user.exception.UserException;
import consome.domain.user.repository.UserRepository;
import consome.infrastructure.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Aspect
@Component
@RequiredArgsConstructor
public class EmailVerifiedAspect {

    private final UserRepository userRepository;

    @Before("@annotation(requireEmailVerified)")
    public void checkEmailVerified(RequireEmailVerified requireEmailVerified) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            return;
        }

        Object principal = authentication.getPrincipal();
        if (!(principal instanceof CustomUserDetails userDetails)) {
            return;
        }

        Long userId = userDetails.getUserId();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserException.NotFound("사용자를 찾을 수 없습니다."));

        if (!user.isEmailVerified()) {
            throw new UserException.EmailNotVerified();
        }
    }
}
