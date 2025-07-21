package consome.application.user;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserRegisterCommand {
    private final String loginId;
    private final String nickname;
    private final String password;

    public static UserRegisterCommand of(String loginId, String nickname, String password) {
        return new UserRegisterCommand(loginId, nickname, password);
    }
}
