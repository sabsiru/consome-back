package consome.domain.user;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@ExtendWith(MockitoExtension.class)
class UserTest {

    @Test
    public void 유저_생성_성공() throws Exception{
        //given
        String loginId = "testUser";
        String nickname = "tester";
        String password = "password123";

        //when
        User user = User.create(loginId, nickname, password);

        //then
        assertThat(user.getLoginId()).isEqualTo(loginId);
        assertThat(user.getNickname()).isEqualTo(nickname);
        assertThat(user.getPassword()).isEqualTo(password);
        assertThat(user.getRole()).isEqualTo(Role.USER);

    }
    @Test
    void 닉네임_변경_성공_tester() {
        // given
        User user = User.create("id", "old", "pw");

        // when
        user.changeNickname("new");

        // then
        assertThat(user.getNickname()).isEqualTo("new");
    }

    @Test
    void 비밀번호_변경_성공_tester() {
        // given
        User user = User.create("id", "nick", "old");

        // when
        user.changePassword("new");

        // then
        assertThat(user.getPassword()).isEqualTo("new");
    }
}