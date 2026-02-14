package consome.domain.user;

import consome.domain.auth.PasswordPolicy;
import consome.domain.common.exception.BusinessException;
import consome.domain.user.exception.UserException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
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


    @Test
    void 로그인아이디_유효성검증_길이_실패() {
        // 4자 미만인 경우 예외 발생 확인
        assertThatThrownBy(() -> User.validateLoginId("abc"))
                .isInstanceOf(UserException.InvalidLoginIdLength.class);
    }

    @Test
    void 로그인아이디_유효성검증_형식_실패() {
        // 영문과 숫자만 포함해야 하므로 특수문자 포함 시 예외 발생 확인
        assertThatThrownBy(() -> User.validateLoginId("test!@#"))
                .isInstanceOf(UserException.InvalidLoginIdFormat.class);
    }

    @Test
    void 닉네임_유효성검증_길이_실패() {
        // 2자 미만인 경우 예외 발생 확인
        assertThatThrownBy(() -> User.validateNickname("a"))
                .isInstanceOf(UserException.InvalidNicknameLength.class);
    }

    @Test
    void 닉네임_유효성검증_형식_실패() {
        // 한글, 영문, 숫자 이외 문자가 포함된 경우 예외 발생 확인
        assertThatThrownBy(() -> User.validateNickname("test!@#"))
                .isInstanceOf(UserException.InvalidNicknameFormat.class);
    }

    @Test
    void 비밀번호_유효성검증_예외발생(){
        // 비밀번호가 8자 미만인 경우 예외 발생 확인
        assertThatThrownBy(() -> PasswordPolicy.validate("short"))
                .isInstanceOf(BusinessException.InvalidPassword.class)
                .hasMessageContaining("비밀번호는 8자 이상 20자 이하이어야 합니다.");

        // 비밀번호가 영문 대소문자와 숫자를 포함하지 않는 경우 예외 발생 확인
        assertThatThrownBy(() -> PasswordPolicy.validate("onlyletters"))
                .isInstanceOf(BusinessException.InvalidPassword.class)
                .hasMessageContaining("비밀번호는 영문 대소문자와 숫자를 포함해야 합니다.");
    }
}