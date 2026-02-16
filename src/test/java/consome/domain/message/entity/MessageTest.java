package consome.domain.message.entity;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class MessageTest {

    @Test
    void 쪽지_생성_성공() {
        Message message = Message.send(1L, 2L, "안녕하세요", 0);

        assertThat(message.getSenderId()).isEqualTo(1L);
        assertThat(message.getReceiverId()).isEqualTo(2L);
        assertThat(message.getContent()).isEqualTo("안녕하세요");
        assertThat(message.getPoint()).isEqualTo(0);
        assertThat(message.isRead()).isFalse();
        assertThat(message.isDeletedBySender()).isFalse();
        assertThat(message.isDeletedByReceiver()).isFalse();
    }

    @Test
    void 쪽지_생성_포인트_선물_포함() {
        Message message = Message.send(1L, 2L, "선물입니다", 100);

        assertThat(message.getPoint()).isEqualTo(100);
        assertThat(message.hasPoint()).isTrue();
    }

    @Test
    void 쪽지_내용_빈값_예외() {
        assertThatThrownBy(() -> Message.send(1L, 2L, "", 0))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("필수");
    }

    @Test
    void 쪽지_내용_2000자_초과_예외() {
        String longContent = "a".repeat(2001);

        assertThatThrownBy(() -> Message.send(1L, 2L, longContent, 0))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("2000자");
    }

    @Test
    void 쪽지_포인트_음수_예외() {
        assertThatThrownBy(() -> Message.send(1L, 2L, "내용", -100))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("0 이상");
    }

    @Test
    void 쪽지_읽음_처리() {
        Message message = Message.send(1L, 2L, "내용", 0);

        message.markAsRead();

        assertThat(message.isRead()).isTrue();
        assertThat(message.getReadAt()).isNotNull();
    }

    @Test
    void 쪽지_중복_읽음_처리_무시() {
        Message message = Message.send(1L, 2L, "내용", 0);
        message.markAsRead();
        var firstReadAt = message.getReadAt();

        message.markAsRead();

        assertThat(message.getReadAt()).isEqualTo(firstReadAt);
    }

    @Test
    void 발신자_삭제() {
        Message message = Message.send(1L, 2L, "내용", 0);

        message.deleteBySender();

        assertThat(message.isDeletedBySender()).isTrue();
        assertThat(message.isDeletedByReceiver()).isFalse();
    }

    @Test
    void 수신자_삭제() {
        Message message = Message.send(1L, 2L, "내용", 0);

        message.deleteByReceiver();

        assertThat(message.isDeletedBySender()).isFalse();
        assertThat(message.isDeletedByReceiver()).isTrue();
    }

    @Test
    void 발신자_접근_가능() {
        Message message = Message.send(1L, 2L, "내용", 0);

        assertThat(message.isSender(1L)).isTrue();
        assertThat(message.isSender(2L)).isFalse();
        assertThat(message.canAccess(1L)).isTrue();
    }

    @Test
    void 수신자_접근_가능() {
        Message message = Message.send(1L, 2L, "내용", 0);

        assertThat(message.isReceiver(2L)).isTrue();
        assertThat(message.isReceiver(1L)).isFalse();
        assertThat(message.canAccess(2L)).isTrue();
    }

    @Test
    void 발신자_삭제_후_접근_불가() {
        Message message = Message.send(1L, 2L, "내용", 0);
        message.deleteBySender();

        assertThat(message.canAccess(1L)).isFalse();
        assertThat(message.canAccess(2L)).isTrue();
    }

    @Test
    void 수신자_삭제_후_접근_불가() {
        Message message = Message.send(1L, 2L, "내용", 0);
        message.deleteByReceiver();

        assertThat(message.canAccess(1L)).isTrue();
        assertThat(message.canAccess(2L)).isFalse();
    }

    @Test
    void 제3자_접근_불가() {
        Message message = Message.send(1L, 2L, "내용", 0);

        assertThat(message.canAccess(3L)).isFalse();
    }
}
