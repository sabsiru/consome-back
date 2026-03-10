package consome.domain.password;

import java.util.Optional;

public interface PasswordResetTokenRepository {

    void saveToken(String token, Long userId);

    Optional<Long> findUserIdByToken(String token);

    void deleteToken(String token);

    void saveCooldown(String email);

    boolean isCooldownActive(String email);
}
