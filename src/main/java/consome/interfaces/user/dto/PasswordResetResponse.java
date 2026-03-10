package consome.interfaces.user.dto;

public record PasswordResetResponse(
        String token
) {
    public static PasswordResetResponse from(String token) {
        return new PasswordResetResponse(token);
    }
}
