package consome.domain.message.entity;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class MessageBlockTest {

    @Test
    void 차단_생성_성공() {
        MessageBlock block = MessageBlock.block(1L, 2L);

        assertThat(block.getBlockerId()).isEqualTo(1L);
        assertThat(block.getBlockedId()).isEqualTo(2L);
        assertThat(block.getCreatedAt()).isNotNull();
    }

    @Test
    void 자기_자신_차단_예외() {
        assertThatThrownBy(() -> MessageBlock.block(1L, 1L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("자기 자신");
    }
}
