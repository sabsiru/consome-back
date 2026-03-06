package consome.interfaces.user.dto;

public record UserRegisterResponse(String message, String verifyToken) {
    public static UserRegisterResponse of(String message, String verifyToken) {
        return new UserRegisterResponse(message, verifyToken);
    }
}
