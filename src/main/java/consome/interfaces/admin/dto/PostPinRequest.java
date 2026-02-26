package consome.interfaces.admin.dto;

public record PostPinRequest(
        Boolean isPinned,
        Integer pinnedOrder
) {
}
