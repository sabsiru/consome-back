package consome.domain.email;

import consome.domain.user.User;
import consome.domain.user.exception.UserException;
import consome.domain.user.repository.UserRepository;
import consome.infrastructure.redis.EmailVerificationRedisRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class EmailVerificationService {

    private final EmailVerificationRedisRepository redisRepository;
    private final UserRepository userRepository;

    public String generateToken(Long userId) {
        String token = UUID.randomUUID().toString();
        redisRepository.saveToken(token, userId);
        return token;
    }

    @Transactional
    public void verifyEmail(String token) {
        Long userId = redisRepository.findUserIdByToken(token)
                .orElseThrow(UserException.InvalidVerificationToken::new);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserException.NotFound("사용자를 찾을 수 없습니다."));

        user.verifyEmail();
        redisRepository.deleteToken(token);
    }

    public void checkCooldown(String email) {
        if (redisRepository.isCooldownActive(email)) {
            throw new UserException.EmailCooldown();
        }
    }

    public void setCooldown(String email) {
        redisRepository.saveCooldown(email);
    }
}
