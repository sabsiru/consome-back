package consome.application.user;


import consome.domain.admin.BoardManager;
import consome.domain.admin.repository.BoardManagerRepository;
import consome.domain.level.LevelInfo;
import consome.domain.level.LevelService;
import consome.domain.point.PointHistoryType;
import consome.domain.point.PointService;
import consome.domain.user.User;
import consome.domain.user.UserService;
import consome.domain.user.exception.UserException;
import consome.domain.user.repository.UserQueryRepository;
import consome.infrastructure.jwt.JwtProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserFacade {

    private final UserService userService;
    private final PointService pointService;
    private final LevelService levelService;
    private final JwtProvider jwtProvider;
    private final BoardManagerRepository boardManagerRepository;
    private final UserQueryRepository userQueryRepository;

    @Transactional
    public Long register(UserRegisterCommand command) {
        User user = userService.register(command.getLoginId(), command.getNickname(), command.getPassword());
        pointService.initialize(user.getId());
        levelService.initialize(user.getId());
        return user.getId();
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
        return new UserLoginResult(user.getId(), user.getLoginId(), user.getNickname(), user.getRole(), currentPoint, level, accessToken, managedBoardIds);
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

}
