package consome.domain.password;

import consome.domain.auth.PasswordEncryptor;
import consome.domain.auth.PasswordPolicy;
import consome.domain.user.User;
import consome.domain.user.exception.UserException;
import consome.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PasswordResetService {

    private final PasswordResetTokenRepository tokenRepository;
    private final UserRepository userRepository;
    private final PasswordEncryptor passwordEncryptor;

    public String generateToken(Long userId) {
        String token = UUID.randomUUID().toString();
        tokenRepository.saveToken(token, userId);
        return token;
    }

    @Transactional
    public void resetPassword(String token, String newPassword) {
        Long userId = tokenRepository.findUserIdByToken(token)
                .orElseThrow(UserException.InvalidResetToken::new);

        PasswordPolicy.validate(newPassword);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserException.NotFound("사용자를 찾을 수 없습니다."));

        String encryptedPassword = passwordEncryptor.encrypt(newPassword);
        user.changePassword(encryptedPassword);
        tokenRepository.deleteToken(token);
    }

    public void checkCooldown(String email) {
        if (tokenRepository.isCooldownActive(email)) {
            throw new UserException.ResetCooldown();
        }
    }

    public void setCooldown(String email) {
        tokenRepository.saveCooldown(email);
    }
}
