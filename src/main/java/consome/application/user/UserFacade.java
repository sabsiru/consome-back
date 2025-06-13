package consome.application.user;


import consome.domain.point.PointService;
import consome.domain.user.User;
import consome.domain.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserFacade {

    private final UserService userService;
    private final PointService pointService;

    public Long create(UserCommand command) {
        User user = userService.create(command.getLoginId(), command.getNickname(), command.getPassword());
        pointService.initialize(user.getId());
        return user.getId();
    }
}
