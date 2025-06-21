package consome.domain.post;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class PostReactionTest {

    @Test
    void 좋아요_생성_테스트() {
        Long postId = 1L;
        Long userId = 2L;
        PostReaction like = PostReaction.like(postId, userId);

        assertThat(like.getPostId()).isEqualTo(postId);
        assertThat(like.getUserId()).isEqualTo(userId);
        assertThat(like.getType()).isEqualTo(ReactionType.LIKE);
        assertThat(like.isDeleted()).isFalse();
        assertThat(like.getCreatedAt()).isNotNull();
        assertThat(like.getUpdatedAt()).isNotNull();
    }

    @Test
    void 싫어요_생성_테스트() {
        Long postId = 1L;
        Long userId = 2L;
        PostReaction dislike = PostReaction.disLike(postId, userId);

        assertThat(dislike.getPostId()).isEqualTo(postId);
        assertThat(dislike.getUserId()).isEqualTo(userId);
        assertThat(dislike.getType()).isEqualTo(ReactionType.DISLIKE);
        assertThat(dislike.isDeleted()).isFalse();
        assertThat(dislike.getCreatedAt()).isNotNull();
        assertThat(dislike.getUpdatedAt()).isNotNull();
    }

    @Test
    void 좋아요_취소_테스트() {
        PostReaction like = PostReaction.like(1L, 2L);
        like.cancel();

        assertThat(like.isDeleted()).isTrue();
        assertThat(like.getUpdatedAt()).isAfterOrEqualTo(like.getCreatedAt());
    }

    @Test
    void 싫어요_취소_테스트() {
        PostReaction dislike = PostReaction.disLike(1L, 2L);
        dislike.cancel();

        assertThat(dislike.isDeleted()).isTrue();
        assertThat(dislike.getUpdatedAt()).isAfterOrEqualTo(dislike.getCreatedAt());
    }
}