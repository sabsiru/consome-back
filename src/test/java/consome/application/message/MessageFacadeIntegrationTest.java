package consome.application.message;

import consome.application.user.UserFacade;
import consome.application.user.UserRegisterCommand;
import consome.domain.message.MessageBlockService;
import consome.domain.message.exception.MessageException;
import consome.domain.message.repository.MessageBlockRepository;
import consome.domain.message.repository.MessageRepository;
import consome.domain.point.PointService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.utility.TestcontainersConfiguration;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Import(TestcontainersConfiguration.class)
@ActiveProfiles("test")
@Transactional
class MessageFacadeIntegrationTest {

    @Autowired
    private MessageFacade messageFacade;

    @Autowired
    private MessageBlockService messageBlockService;

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private MessageBlockRepository messageBlockRepository;

    @Autowired
    private UserFacade userFacade;

    @Autowired
    private PointService pointService;

    private Long senderId;
    private Long receiverId;

    @BeforeEach
    void setUp() {
        messageBlockRepository.deleteAll();
        messageRepository.deleteAll();
        String suffix = UUID.randomUUID().toString().substring(0, 8);
        senderId = userFacade.register(UserRegisterCommand.of("sender" + suffix, "발신자" + suffix, "Password123"));
        receiverId = userFacade.register(UserRegisterCommand.of("receiver" + suffix, "수신자" + suffix, "Password123"));
    }

    @Test
    void 쪽지_발송_성공() {
        SendMessageCommand command = new SendMessageCommand(senderId, receiverId, "안녕하세요", 0);

        MessageResult result = messageFacade.send(command);

        assertThat(result.id()).isNotNull();
        assertThat(result.senderNickname()).contains("발신자");
        assertThat(result.receiverNickname()).contains("수신자");
    }

    @Test
    void 쪽지_발송_포인트_선물() {
        int senderPointBefore = pointService.getCurrentPoint(senderId);
        int receiverPointBefore = pointService.getCurrentPoint(receiverId);
        int giftPoint = 50;

        SendMessageCommand command = new SendMessageCommand(senderId, receiverId, "선물입니다", giftPoint);
        messageFacade.send(command);

        int senderPointAfter = pointService.getCurrentPoint(senderId);
        int receiverPointAfter = pointService.getCurrentPoint(receiverId);

        assertThat(senderPointAfter).isEqualTo(senderPointBefore - giftPoint);
        assertThat(receiverPointAfter).isEqualTo(receiverPointBefore + giftPoint);
    }

    @Test
    void 쪽지_발송_포인트_부족_예외() {
        int currentPoint = pointService.getCurrentPoint(senderId);
        int giftPoint = currentPoint + 100;

        SendMessageCommand command = new SendMessageCommand(senderId, receiverId, "선물", giftPoint);

        assertThatThrownBy(() -> messageFacade.send(command))
                .isInstanceOf(MessageException.InsufficientPoint.class);
    }

    @Test
    void 자기_자신에게_쪽지_발송_예외() {
        SendMessageCommand command = new SendMessageCommand(senderId, senderId, "내용", 0);

        assertThatThrownBy(() -> messageFacade.send(command))
                .isInstanceOf(MessageException.CannotSendToSelf.class);
    }

    @Test
    void 차단된_사용자에게_쪽지_발송_예외() {
        messageBlockService.block(receiverId, senderId);
        SendMessageCommand command = new SendMessageCommand(senderId, receiverId, "내용", 0);

        assertThatThrownBy(() -> messageFacade.send(command))
                .isInstanceOf(MessageException.BlockedUser.class);
    }

    @Test
    void 받은_쪽지_목록_닉네임_포함() {
        messageFacade.send(new SendMessageCommand(senderId, receiverId, "내용", 0));

        Page<MessageListResult> result = messageFacade.getReceivedMessages(receiverId, PageRequest.of(0, 10));

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).otherUserNickname()).contains("발신자");
    }

    @Test
    void 보낸_쪽지_목록_닉네임_포함() {
        messageFacade.send(new SendMessageCommand(senderId, receiverId, "내용", 0));

        Page<MessageListResult> result = messageFacade.getSentMessages(senderId, PageRequest.of(0, 10));

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).otherUserNickname()).contains("수신자");
    }

    @Test
    void 쪽지_상세_조회_읽음_처리() {
        MessageResult sent = messageFacade.send(new SendMessageCommand(senderId, receiverId, "내용", 0));

        MessageResult read = messageFacade.readMessage(sent.id(), receiverId);

        assertThat(read.isRead()).isTrue();
    }

    @Test
    void 사용자_차단() {
        BlockResult result = messageFacade.block(senderId, receiverId);

        assertThat(result.blockedId()).isEqualTo(receiverId);
        assertThat(messageBlockService.isBlocked(senderId, receiverId)).isTrue();
    }

    @Test
    void 사용자_차단_해제() {
        messageFacade.block(senderId, receiverId);

        messageFacade.unblock(senderId, receiverId);

        assertThat(messageBlockService.isBlocked(senderId, receiverId)).isFalse();
    }

    @Test
    void 차단_목록_조회() {
        messageFacade.block(senderId, receiverId);

        Page<BlockResult> result = messageFacade.getBlockList(senderId, PageRequest.of(0, 10));

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).blockedNickname()).contains("수신자");
    }

    @Test
    void 안읽은_쪽지_수_조회() {
        messageFacade.send(new SendMessageCommand(senderId, receiverId, "1", 0));
        messageFacade.send(new SendMessageCommand(senderId, receiverId, "2", 0));

        long count = messageFacade.getUnreadCount(receiverId);

        assertThat(count).isEqualTo(2);
    }
}
