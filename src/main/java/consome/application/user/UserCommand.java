package consome.application.user;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserCommand {
    private String loginId;
    private String nickname;
    private String password;

    public static UserCommand of(String loginId, String nickname, String password) {
        return new UserCommand(loginId, nickname, password);
    }
}
