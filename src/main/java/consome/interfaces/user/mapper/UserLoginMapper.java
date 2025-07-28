package consome.interfaces.user.mapper;

import consome.application.user.UserLoginCommand;
import consome.interfaces.user.dto.UserLoginRequest;

public class UserLoginMapper {
    public static UserLoginCommand toLoginCommand(UserLoginRequest request){
        return new UserLoginCommand(
                request.loginId(),
                request.password()
        );
    }
}
