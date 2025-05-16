package consome.domain.post;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

class PostLikeTest {

    @Test
    void 좋아요_생성_성공_tester() {
        Long postId = 1L;
        Long userId = 10L;

        PostLike like = PostLike.create(postId, userId);

        assertThat(like.getPostId()).isEqualTo(postId);
        assertThat(like.getUserId()).isEqualTo(userId);
        assertThat(like.isDeleted()).isFalse();
        assertThat(like.getCreatedAt()).isNotNull();
        assertThat(like.getUpdatedAt()).isNotNull();
    }

    @Test
    void 좋아요_취소_성공_tester() {
        PostLike like = PostLike.create(1L, 10L);

        like.cancel();

        assertThat(like.isDeleted()).isTrue();
    }

    @Test
    void 좋아요_복원_성공_tester() {
        PostLike like = PostLike.create(1L, 10L);
        like.cancel();

        like.restore();

        assertThat(like.isDeleted()).isFalse();
    }
}