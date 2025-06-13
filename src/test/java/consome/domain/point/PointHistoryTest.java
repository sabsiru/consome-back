package consome.domain.point;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class PointHistoryTest {


    @Test
    public void 회원가입_히스토리_저장_확인() throws Exception{
        // given
        Long userId = 1L;
        int amount = 100;
        PointHistoryType type = PointHistoryType.SIGNUP;
        int beforePoint = 0;
        int afterPoint = 100;

        // when
        PointHistory pointHistory = PointHistory.create(userId,amount, type,beforePoint ,afterPoint);

        // then
        assertThat(pointHistory.getUserId()).isEqualTo(userId);
        assertThat(pointHistory.getAmount()).isEqualTo(amount);
        assertThat(pointHistory.getType()).isEqualTo(type);
        assertThat(pointHistory.getDescription()).isEqualTo(type.getDescription());
        assertThat(pointHistory.getAfterPoint()).isEqualTo(afterPoint);
        assertThat(pointHistory.getCreatedAt()).isBeforeOrEqualTo(LocalDateTime.now());
    }

}
