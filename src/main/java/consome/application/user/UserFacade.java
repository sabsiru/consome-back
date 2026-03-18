package consome.application.user;


import consome.domain.admin.BoardManager;
import consome.domain.admin.repository.BoardManagerRepository;
import consome.domain.email.EmailVerificationService;
import consome.domain.password.PasswordResetService;
import consome.domain.level.LevelInfo;
import consome.domain.level.LevelService;
import consome.domain.point.PointHistoryType;
import consome.domain.point.PointService;
import consome.domain.user.User;
import consome.domain.user.UserService;
import consome.domain.user.exception.UserException;
import consome.domain.user.repository.UserQueryRepository;
import consome.domain.user.repository.UserRepository;
import consome.infrastructure.jwt.JwtProperties;
import consome.infrastructure.jwt.JwtProvider;
import consome.infrastructure.mail.EmailService;
import consome.infrastructure.redis.TokenRedisRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import org.springframework.data.domain.PageRequest;

@Service
@RequiredArgsConstructor
public class UserFacade {

    private final UserService userService;
    private final PointService pointService;
    private final LevelService levelService;
    private final JwtProvider jwtProvider;
    private final JwtProperties jwtProperties;
    private final TokenRedisRepository tokenRedisRepository;
    private final BoardManagerRepository boardManagerRepository;
    private final UserQueryRepository userQueryRepository;
    private final EmailVerificationService emailVerificationService;
    private final EmailService emailService;
    private final UserRepository userRepository;
    private final PasswordResetService passwordResetService;

    @Transactional
    public String register(UserRegisterCommand command) {
        User user = userService.register(command.getLoginId(), command.getNickname(), command.getPassword(), command.getEmail());
        pointService.initialize(user.getId());
        levelService.initialize(user.getId());

        // 인증 메일 발송
        String token = emailVerificationService.generateToken(user.getId());
        emailService.sendVerificationEmail(user.getEmail(), token);
        emailVerificationService.setCooldown(user.getEmail());

        return token;
    }

    @Transactional
    public Long registerWithoutEmail(UserRegisterCommand command) {
        User user = userService.register(command.getLoginId(), command.getNickname(), command.getPassword(), command.getEmail());
        pointService.initialize(user.getId());
        levelService.initialize(user.getId());
        return user.getId();
    }

    @Transactional
    public void verifyEmail(String token) {
        emailVerificationService.verifyEmail(token);
    }

    @Transactional
    public void resendVerificationEmail(Long userId) {
        User user = userService.findById(userId);

        if (user.isEmailVerified()) {
            throw new UserException.AlreadyVerified("이미 인증된 이메일입니다.");
        }

        emailVerificationService.checkCooldown(user.getEmail());

        String token = emailVerificationService.generateToken(userId);
        emailService.sendVerificationEmail(user.getEmail(), token);
        emailVerificationService.setCooldown(user.getEmail());
    }

    @Transactional
    public UserLoginResult login(UserLoginCommand command) {
        User user = userService.login(command.loginId(), command.password());
        int currentPoint = pointService.getCurrentPoint(user.getId());
        int level = LevelInfo.calculateLevel(currentPoint).getLevel();
        List<Long> managedBoardIds = boardManagerRepository.findByUserId(user.getId())
                .stream()
                .map(BoardManager::getBoardId)
                .toList();

        String accessToken = jwtProvider.createAccessToken(user.getId(), user.getRole());
        String refreshToken = jwtProvider.createRefreshToken(user.getId(), user.getRole());
        tokenRedisRepository.saveRefreshToken(user.getId(), refreshToken, jwtProperties.getRefreshTokenExpiration());

        return new UserLoginResult(user.getId(), user.getLoginId(), user.getNickname(), user.getRole(), currentPoint, level, accessToken, refreshToken, managedBoardIds, user.isEmailVerified());
    }

    @Transactional(readOnly = true)
    public UserMeResult getMyInfo(Long userId) {
        User user = userService.findById(userId);
        int currentPoint = pointService.getCurrentPoint(userId);
        int level = LevelInfo.calculateLevel(currentPoint).getLevel();
        List<Long> managedBoardIds = boardManagerRepository.findByUserId(userId)
                .stream()
                .map(BoardManager::getBoardId)
                .toList();
        return new UserMeResult(user.getId(), user.getLoginId(), user.getNickname(), currentPoint, level, user.getRole(), managedBoardIds);
    }

    @Transactional(readOnly = true)
    public UserProfileResult getProfile(Long userId) {
        User user = userService.findById(userId);
        int currentPoint = pointService.getCurrentPoint(userId);
        int level = LevelInfo.calculateLevel(currentPoint).getLevel();
        int postCount = userQueryRepository.countPostsByUserId(userId);
        int commentCount = userQueryRepository.countCommentsByUserId(userId);

        return new UserProfileResult(
                user.getId(),
                user.getNickname(),
                level,
                user.getRole(),
                currentPoint,
                postCount,
                commentCount,
                user.getCreatedAt()
        );
    }

    @Transactional(readOnly = true)
    public Page<UserPostResult> getUserPosts(Long userId, Pageable pageable) {
        userService.findById(userId); // 존재 검증
        return userQueryRepository.findPostsByUserId(userId, pageable);
    }

    @Transactional(readOnly = true)
    public Page<UserCommentResult> getUserComments(Long userId, Pageable pageable) {
        userService.findById(userId); // 존재 검증
        return userQueryRepository.findCommentsByUserId(userId, pageable);
    }

    @Transactional
    public void changePassword(Long userId, String currentPassword, String newPassword) {
        userService.changePassword(userId, currentPassword, newPassword);
    }

    @Transactional
    public String requestPasswordReset(String loginId, String email) {
        User user = userRepository.findByLoginId(loginId)
                .filter(u -> u.getEmail().equals(email))
                .orElseThrow(() -> new UserException.NotFound("아이디 또는 이메일이 일치하지 않습니다."));

        passwordResetService.checkCooldown(user.getEmail());

        String token = passwordResetService.generateToken(user.getId());
        emailService.sendPasswordResetEmail(user.getEmail(), token);
        passwordResetService.setCooldown(user.getEmail());

        return token;
    }

    @Transactional(readOnly = true)
    public String findLoginId(String email) {
        return userService.findLoginIdByEmail(email);
    }

    @Transactional
    public void resetPassword(String token, String newPassword) {
        passwordResetService.resetPassword(token, newPassword);
    }

    @Transactional
    public UserNicknameChangeResult changeNickname(Long userId, String newNickname) {
        int currentPoint = pointService.getCurrentPoint(userId);
        int requiredPoint = PointHistoryType.NICKNAME_CHANGE.getPoint();

        if (currentPoint < requiredPoint) {
            throw new UserException.InsufficientPoint("포인트가 부족합니다 (필요: " + requiredPoint + "P)");
        }

        userService.changeNickname(userId, newNickname);
        int remainingPoint = pointService.penalize(userId, PointHistoryType.NICKNAME_CHANGE);

        return new UserNicknameChangeResult(newNickname, remainingPoint);
    }

    @Transactional(readOnly = true)
    public List<UserSearchResult> searchByNickname(String nickname) {
        UserSearchCommand command = new UserSearchCommand(null, null, null, nickname);
        return userQueryRepository.search(command, PageRequest.of(0, 5)).getContent();
    }

}
