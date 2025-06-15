package consome.domain.post;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class PostViewTest {

    @Test
    void PostView_생성_성공() {
        // given
        Long postId = 1L;
        String userIp = "123.456.789.123";
        Long userId = 42L;

        // when
        PostView view = PostView.create(postId, userIp, userId);

        // then
        assertThat(view.getPostId()).isEqualTo(postId);
        assertThat(view.getUserIp()).isEqualTo(userIp);
        assertThat(view.getUserId()).isEqualTo(userId);
        assertThat(view.getLastViewedAt()).isNotNull();
    }

    @Test
    void PostView_유저ID_null_처리() {
        // given
        Long postId = 1L;
        String userIp = "123.456.789.123";
        Long userId = null;

        // when
        PostView view = PostView.create(postId, userIp, userId);

        // then
        assertThat(view.getUserId()).isEqualTo(0L);

    }
}