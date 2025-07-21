package consome.interfaces.user.mapper;

import consome.interfaces.user.dto.UserRegisterResponse;

public class UserResponseMapper {
    public static UserRegisterResponse toRegisterResponse() {
        return new UserRegisterResponse("회원가입이 성공적으로 완료되었습니다.");
    }
}
