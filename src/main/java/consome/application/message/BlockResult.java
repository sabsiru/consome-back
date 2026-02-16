package consome.application.message;

import consome.domain.message.entity.MessageBlock;

import java.time.LocalDateTime;

public record BlockResult(
        Long blockedId,
        String blockedNickname,
        LocalDateTime createdAt
) {
    public static BlockResult from(MessageBlock block, String blockedNickname) {
        return new BlockResult(
                block.getBlockedId(),
                blockedNickname,
                block.getCreatedAt()
        );
    }
}
