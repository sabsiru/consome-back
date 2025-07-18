package consome.domain.user;

import consome.domain.auth.PasswordEncryptor;
import consome.domain.auth.PasswordPolicy;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncryptor passwordEncryptor;

    public User register(String loginId, String nickname, String password) {
        validateUser(loginId, nickname, password);
        validateDuplicate(loginId, nickname, password);

        String encryptedPassword = passwordEncryptor.encrypt(password);

        User user = User.create(loginId, nickname, encryptedPassword);
        return userRepository.save(user);
    }

    public User login(String loginId, String password) {
        User user = userRepository.findByLoginId(loginId);
        if (!passwordEncryptor.matches(password, user.getPassword())) {
            throw new IllegalArgumentException("사용자 ID 혹은 비밀번호가 일치하지 않습니다.");
        }

        return user;
    }

    public User getById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다. id = " + userId));
    }

    public Boolean validateDuplicate(String loginId, String nickname, String password) {
        boolean loginIdIsEmpty = userRepository.findAllByLoginId(loginId).isEmpty();
        boolean nicknameIsEmpty = userRepository.findAllByNickname(nickname).isEmpty();

        if (!loginIdIsEmpty) {
            throw new IllegalStateException("이미 사용 중인 아이디입니다.");
        }
        if (!nicknameIsEmpty) {
            throw new IllegalStateException("이미 사용 중인 닉네임입니다.");
        }

        return true;
    }

    public boolean validateUser(String loginId, String nickname, String password) {
        User.validateLoginId(loginId);
        User.validateNickname(nickname);
        PasswordPolicy.validate(password);

        return true;
    }
}