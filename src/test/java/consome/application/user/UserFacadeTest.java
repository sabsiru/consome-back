package consome.application.user;

import consome.domain.point.PointService;
import consome.domain.user.User;
import consome.domain.user.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserFacadeTest {

    @Mock
    private UserService userService;

    @Mock
    private PointService pointService;

    @InjectMocks
    private UserFacade userFacade;

    private UserCommand userCommand;
    private User mockUser;

    @BeforeEach
    void setUp() {
        // 테스트용 UserCommand 생성
        userCommand = UserCommand.of("testId", "testid", "password123");

        // 목 User 객체 생성
        mockUser = mock(User.class);
        when(mockUser.getId()).thenReturn(1L);
    }

    @Test
    void 회원가입시_userService_pointService_호출하고_userId를_반환한다() {
        // given
        when(userService.register(anyString(), anyString(), anyString())).thenReturn(mockUser);
        when(pointService.initialize(anyLong())).thenReturn(100);

        // when
        Long userId = userFacade.register(userCommand);

        // then
        assertThat(userId).isEqualTo(1L);

        verify(userService).register(
                userCommand.getLoginId(),
                userCommand.getNickname(),
                userCommand.getPassword()
        );

        verify(pointService).initialize(1L);
    }
}