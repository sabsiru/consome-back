package consome.domain.user;

import consome.domain.auth.PasswordEncryptor;
import consome.domain.auth.PasswordPolicy;
import consome.domain.user.exception.UserException;
import consome.domain.user.repository.UserQueryRepository;
import consome.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserQueryRepository userQueryRepository;
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
                orElseThrow(() -> new UserException.loginFailure(loginId, password));
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


    @Transactional(readOnly = true)
    public Page<UserInfo> findUsers(Pageable pageable) {
        return userQueryRepository.findUsers(pageable);
    }

    public String getNicknameById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new UserException.NotFound("사용자를 찾을 수 없습니다."))
                .getNickname();
    }

    @Transactional
    public void changePassword(Long userId, String currentPassword, String newPassword) {
        User user = findById(userId);

        if (!passwordEncryptor.matches(currentPassword, user.getPassword())) {
            throw new UserException.PasswordMismatch("현재 비밀번호가 일치하지 않습니다.");
        }

        PasswordPolicy.validate(newPassword);
        String encryptedPassword = passwordEncryptor.encrypt(newPassword);
        user.changePassword(encryptedPassword);
    }

    @Transactional
    public void changeNickname(Long userId, String newNickname) {
        User user = findById(userId);

        if (userRepository.existsByNickname(newNickname)) {
            throw new UserException.DuplicateNickname(newNickname);
        }

        User.validateNickname(newNickname);
        user.changeNickname(newNickname);
    }
}