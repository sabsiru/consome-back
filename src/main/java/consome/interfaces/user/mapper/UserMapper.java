package consome.interfaces.user.mapper;

import consome.application.user.UserCommand;
import consome.interfaces.user.dto.UserRegisterRequest;

public class UserMapper {
    public static UserCommand toRegisterCommand(UserRegisterRequest request) {
        return UserCommand.of(request.getLoginId(), request.getNickname(), request.getPassword());
    }
}
