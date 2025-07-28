package consome.interfaces.user.dto;


import jakarta.validation.constraints.NotBlank;

public record UserLoginRequest(
        @NotBlank
        String loginId,

        @NotBlank
        String password
) {
}
