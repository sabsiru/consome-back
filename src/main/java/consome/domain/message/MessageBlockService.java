package consome.domain.message;

import consome.domain.message.entity.MessageBlock;
import consome.domain.message.exception.MessageException;
import consome.domain.message.repository.MessageBlockRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MessageBlockService {

    private final MessageBlockRepository messageBlockRepository;

    @Transactional
    public MessageBlock block(Long blockerId, Long blockedId) {
        if (isBlocked(blockerId, blockedId)) {
            throw new MessageException.AlreadyBlocked();
        }
        MessageBlock block = MessageBlock.block(blockerId, blockedId);
        return messageBlockRepository.save(block);
    }

    @Transactional
    public void unblock(Long blockerId, Long blockedId) {
        MessageBlock block = messageBlockRepository.findByBlockerIdAndBlockedId(blockerId, blockedId)
                .orElseThrow(MessageException.NotBlocked::new);
        messageBlockRepository.delete(block);
    }

    @Transactional(readOnly = true)
    public boolean isBlocked(Long blockerId, Long blockedId) {
        return messageBlockRepository.existsByBlockerIdAndBlockedId(blockerId, blockedId);
    }

    @Transactional(readOnly = true)
    public boolean isBlockedEither(Long userId1, Long userId2) {
        return isBlocked(userId1, userId2) || isBlocked(userId2, userId1);
    }

    @Transactional(readOnly = true)
    public Page<MessageBlock> getBlockList(Long blockerId, Pageable pageable) {
        return messageBlockRepository.findByBlockerId(blockerId, pageable);
    }
}
