package consome.interfaces.user.mapper;

import consome.application.user.UserRegisterCommand;
import consome.interfaces.user.dto.UserRegisterRequest;

public class UserRegisterMapper {
    public static UserRegisterCommand toRegisterCommand(UserRegisterRequest request) {
        return new UserRegisterCommand(
                request.loginId(),
                request.nickname(),
                request.password()
        );
    }
}
