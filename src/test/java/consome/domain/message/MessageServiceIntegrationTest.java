package consome.domain.message;

import consome.application.user.UserFacade;
import consome.application.user.UserRegisterCommand;
import consome.domain.message.entity.Message;
import consome.domain.message.exception.MessageException;
import consome.domain.message.repository.MessageRepository;
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
class MessageServiceIntegrationTest {

    @Autowired
    private MessageService messageService;

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private UserFacade userFacade;

    private Long senderId;
    private Long receiverId;

    @BeforeEach
    void setUp() {
        messageRepository.deleteAll();
        String suffix = UUID.randomUUID().toString().substring(0, 8);
        senderId = userFacade.register(UserRegisterCommand.of("sender" + suffix, "발신자" + suffix, "Password123"));
        receiverId = userFacade.register(UserRegisterCommand.of("receiver" + suffix, "수신자" + suffix, "Password123"));
    }

    @Test
    void 쪽지_발송_성공() {
        Message message = messageService.send(senderId, receiverId, "안녕하세요", 0);

        assertThat(message.getId()).isNotNull();
        assertThat(message.getSenderId()).isEqualTo(senderId);
        assertThat(message.getReceiverId()).isEqualTo(receiverId);
        assertThat(message.getContent()).isEqualTo("안녕하세요");
    }

    @Test
    void 받은_쪽지_목록_조회() {
        messageService.send(senderId, receiverId, "첫번째", 0);
        messageService.send(senderId, receiverId, "두번째", 0);

        Page<Message> messages = messageService.getReceivedMessages(receiverId, PageRequest.of(0, 10));

        assertThat(messages.getContent()).hasSize(2);
    }

    @Test
    void 보낸_쪽지_목록_조회() {
        messageService.send(senderId, receiverId, "첫번째", 0);
        messageService.send(senderId, receiverId, "두번째", 0);

        Page<Message> messages = messageService.getSentMessages(senderId, PageRequest.of(0, 10));

        assertThat(messages.getContent()).hasSize(2);
    }

    @Test
    void 쪽지_읽음_처리() {
        Message message = messageService.send(senderId, receiverId, "내용", 0);

        Message readMessage = messageService.readMessage(message.getId(), receiverId);

        assertThat(readMessage.isRead()).isTrue();
        assertThat(readMessage.getReadAt()).isNotNull();
    }

    @Test
    void 발신자가_읽어도_읽음_처시_안됨() {
        Message message = messageService.send(senderId, receiverId, "내용", 0);

        Message readMessage = messageService.readMessage(message.getId(), senderId);

        assertThat(readMessage.isRead()).isFalse();
    }

    @Test
    void 쪽지_삭제_발신자() {
        Message message = messageService.send(senderId, receiverId, "내용", 0);

        messageService.deleteMessage(message.getId(), senderId);

        Page<Message> senderMessages = messageService.getSentMessages(senderId, PageRequest.of(0, 10));
        assertThat(senderMessages.getContent()).isEmpty();

        Page<Message> receiverMessages = messageService.getReceivedMessages(receiverId, PageRequest.of(0, 10));
        assertThat(receiverMessages.getContent()).hasSize(1);
    }

    @Test
    void 쪽지_삭제_수신자() {
        Message message = messageService.send(senderId, receiverId, "내용", 0);

        messageService.deleteMessage(message.getId(), receiverId);

        Page<Message> senderMessages = messageService.getSentMessages(senderId, PageRequest.of(0, 10));
        assertThat(senderMessages.getContent()).hasSize(1);

        Page<Message> receiverMessages = messageService.getReceivedMessages(receiverId, PageRequest.of(0, 10));
        assertThat(receiverMessages.getContent()).isEmpty();
    }

    @Test
    void 안읽은_쪽지_수_조회() {
        messageService.send(senderId, receiverId, "1", 0);
        messageService.send(senderId, receiverId, "2", 0);
        Message readMessage = messageService.send(senderId, receiverId, "3", 0);
        messageService.readMessage(readMessage.getId(), receiverId);

        long unreadCount = messageService.countUnread(receiverId);

        assertThat(unreadCount).isEqualTo(2);
    }

    @Test
    void 존재하지_않는_쪽지_조회_예외() {
        assertThatThrownBy(() -> messageService.findById(999L))
                .isInstanceOf(MessageException.NotFound.class);
    }

    @Test
    void 권한_없는_쪽지_접근_예외() {
        Message message = messageService.send(senderId, receiverId, "내용", 0);
        String suffix = UUID.randomUUID().toString().substring(0, 8);
        Long otherUserId = userFacade.register(UserRegisterCommand.of("other" + suffix, "다른유저" + suffix, "Password123"));

        assertThatThrownBy(() -> messageService.readMessage(message.getId(), otherUserId))
                .isInstanceOf(MessageException.AccessDenied.class);
    }
}
