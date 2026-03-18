package consome.interfaces.user.dto;

public record FindIdResponse(String maskedLoginId) {

    public static FindIdResponse from(String maskedLoginId) {
        return new FindIdResponse(maskedLoginId);
    }
}
