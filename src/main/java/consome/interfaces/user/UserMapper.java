package consome.interfaces.user;

import consome.application.user.UserCommand;

public class UserMapper {
    public static UserCommand toCommand(UserRegisterRequest request) {
        return UserCommand.of(request.getLoginId(), request.getNickname(), request.getPassword());
    }
}
