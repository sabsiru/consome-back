package consome.interfaces.user.mapper;

import consome.application.user.UserLoginResult;
import consome.interfaces.user.dto.UserLoginResponse;

public class UserLoginResponseMapper {
    public static UserLoginResponse toLoginResponse(UserLoginResult result) {
        return UserLoginResponse.from(result);
    }
}
