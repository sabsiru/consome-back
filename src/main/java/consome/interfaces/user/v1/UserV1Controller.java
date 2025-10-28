package consome.interfaces.user.v1;


import consome.application.user.UserFacade;
import consome.application.user.UserLoginCommand;
import consome.application.user.UserLoginResult;
import consome.application.user.UserMeResult;
import consome.infrastructure.security.CustomUserDetails;
import consome.interfaces.user.dto.*;
import consome.interfaces.user.mapper.*;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
public class UserV1Controller {

    private final UserFacade userFacade;

    @PostMapping
    public ResponseEntity<UserRegisterResponse> register(@RequestBody @Valid UserRegisterRequest request) {
        userFacade.register(UserRegisterMapper.toRegisterCommand(request));
        UserRegisterResponse response = UserRegisterResponseMapper.toRegisterResponse();
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<UserLoginResponse> login(@RequestBody @Valid UserLoginRequest request) {
        UserLoginCommand command = UserLoginMapper.toLoginCommand(request);
        UserLoginResult result = userFacade.login(command);
        UserLoginResponse response = UserLoginResponseMapper.toLoginResponse(result);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/me")
    public ResponseEntity<UserMeResponse> getMyInfo(@AuthenticationPrincipal CustomUserDetails userDetails) {
        UserMeResult result = userFacade.getMyInfo(userDetails.getUserId());
        UserMeResponse response = UserMeResponseMapper.toResponse(result);
        return ResponseEntity.ok(response);
    }
}
