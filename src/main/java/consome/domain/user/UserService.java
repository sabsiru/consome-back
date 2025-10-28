package consome.domain.user;

import consome.domain.auth.PasswordEncryptor;
import consome.domain.auth.PasswordPolicy;
import consome.domain.user.exception.UserException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncryptor passwordEncryptor;

    public User register(String loginId, String nickname, String password) {
        validateDuplicate(loginId, nickname);
        validateUser(loginId, nickname, password);

        String encryptedPassword = passwordEncryptor.encrypt(password);

        User user = User.create(loginId, nickname, encryptedPassword);
        return userRepository.save(user);
    }

    public User login(String loginId, String password) {
        User user = userRepository.findByLoginId(loginId).
                orElseThrow(()-> new UserException.loginFailure(loginId, password));
        if (!passwordEncryptor.matches(password, user.getPassword())) {
            throw new UserException.loginFailure(loginId, password);
        }

        return user;
    }

    public Boolean validateDuplicate(String loginId, String nickname) {

        if (userRepository.existsByLoginId(loginId)) {
            throw new UserException.DuplicateLoginId(loginId);
        }
        if (userRepository.existsByNickname(nickname)) {
            throw new UserException.DuplicateNickname(nickname) {
            };
        }

        return true;
    }

    public boolean validateUser(String loginId, String nickname, String password) {
        User.validateLoginId(loginId);
        User.validateNickname(nickname);
        PasswordPolicy.validate(password);

        return true;
    }

    @Transactional(readOnly = true)
    public User findById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new UserException.NotFound("사용자를 찾을 수 없습니다."));
    }

    public boolean existsByRole() {
        return userRepository.existsByRole(Role.ADMIN);
    }
}