package consome.interfaces.user.v1;


import consome.application.user.UserFacade;
import consome.domain.user.User;
import consome.interfaces.user.dto.UserLoginRequest;
import consome.interfaces.user.dto.UserLoginResponse;
import consome.interfaces.user.mapper.UserMapper;
import consome.interfaces.user.dto.UserRegisterRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
public class UserV1Controller {

    private final UserFacade userFacade;

    @PostMapping("/")
    public Long register(@RequestBody @Valid UserRegisterRequest userRegisterRequest) {
        return userFacade.register(UserMapper.toRegisterCommand(userRegisterRequest));
    }

    @PostMapping("/login")
    public UserLoginResponse login(@RequestBody UserLoginRequest request){
        User login = userFacade.login(request.getLoginId(), request.getPassword());

        return UserLoginResponse.from(login);
    }
}
