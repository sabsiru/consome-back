package consome.application.user;


import consome.domain.admin.BoardManager;
import consome.domain.admin.repository.BoardManagerRepository;
import consome.domain.comment.Comment;
import consome.domain.comment.CommentReaction;
import consome.domain.comment.CommentService;
import consome.domain.level.LevelInfo;
import consome.domain.level.LevelService;
import consome.domain.point.PointHistoryType;
import consome.domain.point.PointService;
import consome.domain.post.entity.Post;
import consome.domain.post.PostService;
import consome.domain.post.entity.PostStat;
import consome.domain.post.ReactionType;
import consome.domain.user.User;
import consome.domain.user.UserService;
import consome.infrastructure.jwt.JwtProvider;
import lombok.RequiredArgsConstructor;
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

}
