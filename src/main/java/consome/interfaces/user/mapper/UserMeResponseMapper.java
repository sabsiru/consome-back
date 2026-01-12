package consome.interfaces.user.mapper;

import consome.application.user.UserMeResult;
import consome.interfaces.user.dto.UserMeResponse;

public class UserMeResponseMapper {
    public static UserMeResponse toResponse(UserMeResult result) {
        return new UserMeResponse(
                result.userId(),
                result.loginId(),
                result.nickname(),
                result.point(),
                result.role()
        );
    }
}