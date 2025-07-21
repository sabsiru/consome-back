package consome.interfaces.user.v1;


import consome.application.user.UserFacade;
import consome.domain.user.User;
import consome.interfaces.user.dto.UserLoginRequest;
import consome.interfaces.user.dto.UserLoginResponse;
import consome.interfaces.user.mapper.UserRegisterMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import consome.interfaces.user.dto.UserRegisterResponse;
import consome.interfaces.user.mapper.UserResponseMapper;
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

    @PostMapping
    public ResponseEntity<UserRegisterResponse> register(@RequestBody @Valid UserRegisterRequest request) {
        userFacade.register(UserRegisterMapper.toRegisterCommand(request));
        UserRegisterResponse response = UserResponseMapper.toRegisterResponse();
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/login")
    public UserLoginResponse login(@RequestBody UserLoginRequest request) {
        User login = userFacade.login(request.getLoginId(), request.getPassword());

        return UserLoginResponse.from(login);
    }
}
