package consome.domain.message;

import consome.domain.message.entity.Message;
import consome.domain.message.exception.MessageException;
import consome.domain.message.repository.MessageQueryRepository;
import consome.domain.message.repository.MessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MessageService {

    private final MessageRepository messageRepository;
    private final MessageQueryRepository messageQueryRepository;

    @Transactional
    public Message send(Long senderId, Long receiverId, String content, int point) {
        Message message = Message.send(senderId, receiverId, content, point);
        return messageRepository.save(message);
    }

    @Transactional(readOnly = true)
    public Page<Message> getReceivedMessages(Long receiverId, Pageable pageable) {
        return messageQueryRepository.findReceivedMessages(receiverId, pageable);
    }

    @Transactional(readOnly = true)
    public Page<Message> getSentMessages(Long senderId, Pageable pageable) {
        return messageQueryRepository.findSentMessages(senderId, pageable);
    }

    @Transactional
    public Message readMessage(Long messageId, Long userId) {
        Message message = findById(messageId);
        validateAccess(message, userId);

        if (message.isReceiver(userId)) {
            message.markAsRead();
        }
        return message;
    }

    @Transactional
    public Message deleteMessage(Long messageId, Long userId) {
        Message message = findById(messageId);
        validateAccess(message, userId);

        if (message.isSender(userId)) {
            message.deleteBySender();
        } else if (message.isReceiver(userId)) {
            message.deleteByReceiver();
        }
        return message;
    }

    @Transactional(readOnly = true)
    public long countUnread(Long receiverId) {
        return messageRepository.countUnreadByReceiverId(receiverId);
    }

    public Message findById(Long messageId) {
        return messageRepository.findById(messageId)
                .orElseThrow(() -> new MessageException.NotFound(messageId));
    }

    private void validateAccess(Message message, Long userId) {
        if (!message.canAccess(userId)) {
            throw new MessageException.AccessDenied();
        }
    }
}
