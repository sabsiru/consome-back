package consome.domain.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public User register(String loginId, String nickname, String password) {
        userValidate(loginId, nickname);
        User user = User.create(loginId, nickname, password);
        return userRepository.save(user);
    }

    public User getById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다. id = " + userId));
    }

    public Boolean userValidate(String loginId, String nickname) {
        boolean loginIdIsEmpty = userRepository.findByLoginId(loginId).isEmpty();
        boolean nicknameIsEmpty = userRepository.findByNickname(nickname).isEmpty();

        if (!loginIdIsEmpty) {
            throw new IllegalStateException("이미 사용 중인 아이디입니다.");
        }
        if (!nicknameIsEmpty) {
            throw new IllegalStateException("이미 사용 중인 닉네임입니다.");
        }

        return true;
    }
}