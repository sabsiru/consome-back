package consome.domain.user;

import consome.domain.auth.PasswordEncryptor;
import consome.domain.auth.PasswordPolicy;
import consome.domain.user.exception.UserException;
import consome.domain.user.repository.SuspensionHistoryRepository;
import consome.domain.user.repository.UserQueryRepository;
import consome.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserQueryRepository userQueryRepository;
    private final SuspensionHistoryRepository suspensionHistoryRepository;
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

        // 제재 상태 체크
        checkSuspension(user);

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

    @Transactional
    public User suspend(Long userId, SuspensionType type, String reason, Long reportId, Long adminId) {
        User user = findById(userId);

        // 정지 시작/종료 시점 계산
        LocalDateTime startAt = LocalDateTime.now();
        LocalDateTime endAt = null;
        if (!type.isPermanent()) {
            LocalDateTime baseTime = user.isSuspended() && user.getSuspendedUntil() != null
                    ? user.getSuspendedUntil()
                    : startAt;
            endAt = baseTime.plusDays(type.getDays());
        }

        // 이력 저장
        SuspensionHistory history = SuspensionHistory.create(
                userId, reportId, adminId, type, reason, startAt, endAt);
        suspensionHistoryRepository.save(history);

        // 유저 상태 업데이트
        user.suspend(type, reason);
        return user;
    }

    @Transactional
    public User unsuspend(Long userId) {
        User user = findById(userId);
        user.unsuspend();
        return user;
    }

    // 로그인 시 제재 체크
    public void checkSuspension(User user) {
        if (!user.isSuspended()) return;

        if (user.isPermanentlyBanned()) {
            throw new UserException.Banned(user.getSuspendReason());
        }

        String until = user.getSuspendedUntil().toString().replace("T", " ").substring(0, 16);
        throw new UserException.Suspended(user.getSuspendReason(), until);
    }
}