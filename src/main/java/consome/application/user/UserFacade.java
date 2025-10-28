package consome.application.user;


import consome.domain.comment.Comment;
import consome.domain.comment.CommentReaction;
import consome.domain.comment.CommentService;
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

@Service
@RequiredArgsConstructor
public class UserFacade {

    private final UserService userService;
    private final PointService pointService;
    private final JwtProvider jwtProvider;

    @Transactional
    public Long register(UserRegisterCommand command) {
        User user = userService.register(command.getLoginId(), command.getNickname(), command.getPassword());
        pointService.initialize(user.getId());
        return user.getId();
    }

    @Transactional
    public UserLoginResult login(UserLoginCommand command) {
        User user = userService.login(command.loginId(), command.password());
        int currentPoint = pointService.getCurrentPoint(user.getId());

        String accessToken = jwtProvider.createAccessToken(user.getId(), user.getRole());
        return new UserLoginResult(user.getId(), user.getLoginId(), user.getNickname(), user.getRole(), currentPoint, accessToken);
    }

    @Transactional(readOnly = true)
    public UserMeResult getMyInfo(Long userId) {
        User user = userService.findById(userId);
        int currentPoint = pointService.getCurrentPoint(userId); // ✅ 따로 조회
        return new UserMeResult(user.getLoginId(), user.getNickname(), currentPoint, user.getRole());
    }

}
