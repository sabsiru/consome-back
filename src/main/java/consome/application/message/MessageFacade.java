package consome.application.message;

import consome.domain.message.MessageBlockService;
import consome.domain.message.MessageService;
import consome.domain.message.entity.Message;
import consome.domain.message.entity.MessageBlock;
import consome.domain.message.exception.MessageException;
import consome.domain.point.Point;
import consome.domain.point.PointHistory;
import consome.domain.point.PointHistoryType;
import consome.domain.point.PointService;
import consome.domain.point.repository.PointHistoryRepository;
import consome.domain.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MessageFacade {

    private final MessageService messageService;
    private final MessageBlockService messageBlockService;
    private final UserService userService;
    private final PointService pointService;
    private final PointHistoryRepository pointHistoryRepository;

    @Transactional
    public MessageResult send(SendMessageCommand command) {
        validateSendMessage(command);

        // 포인트 선물 처리
        if (command.point() > 0) {
            transferPoint(command.senderId(), command.receiverId(), command.point());
        }

        Message message = messageService.send(
                command.senderId(),
                command.receiverId(),
                command.content(),
                command.point()
        );

        return toMessageResult(message);
    }

    @Transactional(readOnly = true)
    public Page<MessageListResult> getReceivedMessages(Long userId, Pageable pageable) {
        return messageService.getReceivedMessages(userId, pageable)
                .map(message -> MessageListResult.forReceived(
                        message,
                        userService.getNicknameById(message.getSenderId())
                ));
    }

    @Transactional(readOnly = true)
    public Page<MessageListResult> getSentMessages(Long userId, Pageable pageable) {
        return messageService.getSentMessages(userId, pageable)
                .map(message -> MessageListResult.forSent(
                        message,
                        userService.getNicknameById(message.getReceiverId())
                ));
    }

    @Transactional
    public MessageResult readMessage(Long messageId, Long userId) {
        Message message = messageService.readMessage(messageId, userId);
        return toMessageResult(message);
    }

    @Transactional
    public void deleteMessage(Long messageId, Long userId) {
        messageService.deleteMessage(messageId, userId);
    }

    @Transactional(readOnly = true)
    public long getUnreadCount(Long userId) {
        return messageService.countUnread(userId);
    }

    @Transactional
    public BlockResult block(Long blockerId, Long blockedId) {
        userService.findById(blockedId); // 존재 검증
        MessageBlock block = messageBlockService.block(blockerId, blockedId);
        return BlockResult.from(block, userService.getNicknameById(blockedId));
    }

    @Transactional
    public void unblock(Long blockerId, Long blockedId) {
        messageBlockService.unblock(blockerId, blockedId);
    }

    @Transactional(readOnly = true)
    public Page<BlockResult> getBlockList(Long blockerId, Pageable pageable) {
        return messageBlockService.getBlockList(blockerId, pageable)
                .map(block -> BlockResult.from(
                        block,
                        userService.getNicknameById(block.getBlockedId())
                ));
    }

    private void validateSendMessage(SendMessageCommand command) {
        // 자기 자신에게 발송 금지
        if (command.senderId().equals(command.receiverId())) {
            throw new MessageException.CannotSendToSelf();
        }

        // 수신자 존재 검증
        userService.findById(command.receiverId());

        // 차단 여부 확인 (양방향)
        if (messageBlockService.isBlockedEither(command.senderId(), command.receiverId())) {
            throw new MessageException.BlockedUser();
        }

        // 포인트 잔액 확인
        if (command.point() > 0) {
            int currentPoint = pointService.getCurrentPoint(command.senderId());
            if (currentPoint < command.point()) {
                throw new MessageException.InsufficientPoint();
            }
        }
    }

    private void transferPoint(Long senderId, Long receiverId, int amount) {
        // 발신자 포인트 차감 (락 적용)
        Point senderPoint = pointService.findPointByUserIdForUpdate(senderId);
        int beforeSender = senderPoint.getUserPoint();
        senderPoint.penalize(amount);
        pointHistoryRepository.save(PointHistory.create(
                senderId, amount, PointHistoryType.GIFT_SEND, beforeSender, senderPoint.getUserPoint()
        ));

        // 수신자 포인트 증가 (락 적용)
        Point receiverPoint = pointService.findPointByUserIdForUpdate(receiverId);
        int beforeReceiver = receiverPoint.getUserPoint();
        receiverPoint.earn(amount);
        pointHistoryRepository.save(PointHistory.create(
                receiverId, amount, PointHistoryType.GIFT_RECEIVE, beforeReceiver, receiverPoint.getUserPoint()
        ));
    }

    private MessageResult toMessageResult(Message message) {
        return MessageResult.from(
                message,
                userService.getNicknameById(message.getSenderId()),
                userService.getNicknameById(message.getReceiverId())
        );
    }
}
