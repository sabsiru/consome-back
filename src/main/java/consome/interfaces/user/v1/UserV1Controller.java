package consome.interfaces.user.v1;


import consome.application.user.*;
import consome.infrastructure.security.CustomUserDetails;
import consome.interfaces.user.dto.*;
import consome.interfaces.user.mapper.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
public class UserV1Controller {

    private final UserFacade userFacade;

    @PostMapping
    public ResponseEntity<UserRegisterResponse> register(@RequestBody @Valid UserRegisterRequest request) {
        String verifyToken = userFacade.register(UserRegisterMapper.toRegisterCommand(request));
        UserRegisterResponse response = UserRegisterResponseMapper.toRegisterResponse(verifyToken);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<UserLoginResponse> login(@RequestBody @Valid UserLoginRequest request) {
        UserLoginCommand command = UserLoginMapper.toLoginCommand(request);
        UserLoginResult result = userFacade.login(command);
        UserLoginResponse response = UserLoginResponseMapper.toLoginResponse(result);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/search")
    public ResponseEntity<List<UserNicknameSearchResponse>> searchByNickname(
            @RequestParam String nickname
    ) {
        List<UserNicknameSearchResponse> response = userFacade.searchByNickname(nickname)
                .stream()
                .map(UserNicknameSearchResponse::from)
                .toList();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/me")
    public ResponseEntity<UserMeResponse> getMyInfo(@AuthenticationPrincipal CustomUserDetails userDetails) {
        UserMeResult result = userFacade.getMyInfo(userDetails.getUserId());
        UserMeResponse response = UserMeResponseMapper.toResponse(result);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<UserProfileResponse> getProfile(@PathVariable Long userId) {
        UserProfileResult result = userFacade.getProfile(userId);
        return ResponseEntity.ok(UserProfileResponse.from(result));
    }

    @GetMapping("/{userId}/posts")
    public ResponseEntity<UserPostListResponse> getUserPosts(
            @PathVariable Long userId,
            @PageableDefault(size = 50) Pageable pageable
    ) {
        Page<UserPostResult> result = userFacade.getUserPosts(userId, pageable);
        return ResponseEntity.ok(UserPostListResponse.from(result));
    }

    @GetMapping("/{userId}/comments")
    public ResponseEntity<UserCommentListResponse> getUserComments(
            @PathVariable Long userId,
            @PageableDefault(size = 50) Pageable pageable
    ) {
        Page<UserCommentResult> result = userFacade.getUserComments(userId, pageable);
        return ResponseEntity.ok(UserCommentListResponse.from(result));
    }

    @PutMapping("/me/password")
    public ResponseEntity<Void> changePassword(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody @Valid PasswordChangeRequest request
    ) {
        userFacade.changePassword(userDetails.getUserId(), request.currentPassword(), request.newPassword());
        return ResponseEntity.ok().build();
    }

    @PutMapping("/me/nickname")
    public ResponseEntity<NicknameChangeResponse> changeNickname(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody @Valid NicknameChangeRequest request
    ) {
        UserNicknameChangeResult result = userFacade.changeNickname(userDetails.getUserId(), request.nickname());
        return ResponseEntity.ok(NicknameChangeResponse.from(result));
    }

    @PostMapping("/find-id")
    public ResponseEntity<FindIdResponse> findId(@RequestBody @Valid FindIdRequest request) {
        String maskedLoginId = userFacade.findLoginId(request.email());
        return ResponseEntity.ok(FindIdResponse.from(maskedLoginId));
    }

    @PostMapping("/password/reset-request")
    public ResponseEntity<PasswordResetResponse> requestPasswordReset(
            @RequestBody @Valid PasswordResetRequest request
    ) {
        String token = userFacade.requestPasswordReset(request.loginId(), request.email());
        return ResponseEntity.ok(PasswordResetResponse.from(token));
    }

    @PutMapping("/password/reset")
    public ResponseEntity<Void> resetPassword(
            @RequestBody @Valid PasswordResetConfirmRequest request
    ) {
        userFacade.resetPassword(request.token(), request.newPassword());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/email/verify")
    public ResponseEntity<Void> verifyEmail(@RequestParam String token) {
        userFacade.verifyEmail(token);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/email/resend")
    public ResponseEntity<Void> resendVerificationEmail(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        userFacade.resendVerificationEmail(userDetails.getUserId());
        return ResponseEntity.ok().build();
    }
}
