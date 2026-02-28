package consome.interfaces.admin.dto;

import consome.application.admin.PostPinResult;

public record PostPinResponse(
        Long postId,
        Boolean isPinned,
        Integer pinnedOrder,
        String message
) {
    public static PostPinResponse from(PostPinResult result) {
        return new PostPinResponse(
                result.postId(),
                result.isPinned(),
                result.pinnedOrder(),
                result.message()
        );
    }
}
