package consome.application.admin;

public record PostPinResult(
        Long postId,
        Boolean isPinned,
        Integer pinnedOrder,
        String message
) {
}
