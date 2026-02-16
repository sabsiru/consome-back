package consome.application.message;

public record SendMessageCommand(
        Long senderId,
        Long receiverId,
        String content,
        int point
) {
    public SendMessageCommand {
        if (point < 0) {
            throw new IllegalArgumentException("포인트는 0 이상이어야 합니다.");
        }
    }
}
