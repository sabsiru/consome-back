package consome.application.user;

import consome.domain.email.EmailVerificationService;
import consome.domain.password.PasswordResetService;
import consome.domain.point.PointService;
import consome.domain.user.User;
import consome.domain.user.UserService;
import consome.domain.user.exception.UserException;
import consome.domain.user.repository.UserRepository;
import consome.domain.level.LevelService;
import consome.infrastructure.mail.EmailService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserFacadeTest {

    @Mock
    private UserService userService;

    @Mock
    private PointService pointService;

    @Mock
    private LevelService levelService;

    @Mock
    private EmailVerificationService emailVerificationService;

    @Mock
    private EmailService emailService;

    @Mock
    private PasswordResetService passwordResetService;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserFacade userFacade;

    private UserRegisterCommand userRegisterCommand;
    private User mockUser;

    @BeforeEach
    void setUp() {
        // 테스트용 UserRegisterCommand 생성
        userRegisterCommand = UserRegisterCommand.of("testId", "testid", "password123", "test@test.com");

        // 목 User 객체 생성
        mockUser = mock(User.class);
        lenient().when(mockUser.getId()).thenReturn(1L);
    }

    @Test
    void 회원가입시_userService_pointService_호출하고_userId를_반환한다() {
        // given
        when(userService.register(anyString(), anyString(), anyString(), anyString())).thenReturn(mockUser);
        // when
        Long userId = userFacade.registerWithoutEmail(userRegisterCommand);

        // then
        assertThat(userId).isEqualTo(1L);

        verify(userService).register(
                userRegisterCommand.getLoginId(),
                userRegisterCommand.getNickname(),
                userRegisterCommand.getPassword(),
                userRegisterCommand.getEmail()
        );

        verify(pointService).initialize(1L);
        verifyNoInteractions(emailVerificationService, emailService);
    }

    @Test
    void 비밀번호_재설정_요청시_아이디와_이메일이_일치하면_토큰을_생성하고_이메일을_발송한다() {
        // given
        String loginId = "testId";
        String email = "test@test.com";
        when(userRepository.findByLoginId(loginId)).thenReturn(Optional.of(mockUser));
        when(mockUser.getEmail()).thenReturn(email);
        when(passwordResetService.generateToken(1L)).thenReturn("reset-token");

        // when
        String token = userFacade.requestPasswordReset(loginId, email);

        // then
        assertThat(token).isEqualTo("reset-token");
        verify(passwordResetService).checkCooldown(email);
        verify(passwordResetService).generateToken(1L);
        verify(emailService).sendPasswordResetEmail(email, "reset-token");
        verify(passwordResetService).setCooldown(email);
    }

    @Test
    void 비밀번호_재설정_요청시_아이디가_없으면_예외가_발생한다() {
        // given
        when(userRepository.findByLoginId("wrongId")).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> userFacade.requestPasswordReset("wrongId", "test@test.com"))
                .isInstanceOf(UserException.NotFound.class);
    }

    @Test
    void 비밀번호_재설정_요청시_이메일이_불일치하면_예외가_발생한다() {
        // given
        when(userRepository.findByLoginId("testId")).thenReturn(Optional.of(mockUser));
        when(mockUser.getEmail()).thenReturn("real@test.com");

        // when & then
        assertThatThrownBy(() -> userFacade.requestPasswordReset("testId", "wrong@test.com"))
                .isInstanceOf(UserException.NotFound.class);
    }

    @Test
    void 아이디_찾기_요청시_마스킹된_아이디를_반환한다() {
        // given
        String email = "test@test.com";
        when(userService.findLoginIdByEmail(email)).thenReturn("te***d");

        // when
        String maskedId = userFacade.findLoginId(email);

        // then
        assertThat(maskedId).isEqualTo("te***d");
        verify(userService).findLoginIdByEmail(email);
    }

    @Test
    void 비밀번호_재설정시_토큰으로_비밀번호를_변경한다() {
        // given
        String token = "reset-token";
        String newPassword = "NewPass123";

        // when
        userFacade.resetPassword(token, newPassword);

        // then
        verify(passwordResetService).resetPassword(token, newPassword);
    }
}