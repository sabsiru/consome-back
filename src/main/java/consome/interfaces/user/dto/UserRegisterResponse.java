package consome.interfaces.user.dto;

public record UserRegisterResponse(String message) {
    public static UserRegisterResponse of(String message) {
        return new UserRegisterResponse(message);
    }
}
