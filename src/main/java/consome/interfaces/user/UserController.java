package consome.interfaces.user;


import consome.application.user.UserFacade;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {

    private final UserFacade userFacade;

    @PostMapping("/register")
    public Long register(@RequestBody @Valid UserRegisterRequest userRegisterRequest) {
        return userFacade.register(UserMapper.toCommand(userRegisterRequest));
    }
}
