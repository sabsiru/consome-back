package consome.interfaces.message.dto;

import consome.application.message.BlockResult;

import java.time.LocalDateTime;

public record BlockResponse(
        Long blockedId,
        String blockedNickname,
        LocalDateTime createdAt
) {
    public static BlockResponse from(BlockResult result) {
        return new BlockResponse(
                result.blockedId(),
                result.blockedNickname(),
                result.createdAt()
        );
    }
}
