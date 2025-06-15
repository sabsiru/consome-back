package consome.domain.post;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

@SpringBootTest
@Transactional
class PostServiceIntegrationTest {

    private static final Logger log = LoggerFactory.getLogger(PostServiceIntegrationTest.class);
    @Autowired
    private PostService postService;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private PostStatRepository statRepository;

    @Autowired
    private PostLikeRepository likeRepository;

    @Test
    void 게시글_작성_성공() {
        // given
        Long boardId = 1L;
        Long categoryId = 2L;
        String title = "테스트 제목";
        String content = "테스트 내용";
        Long authorId = 100L;

        Post post = Post.write(boardId, categoryId, authorId, title, content);

        // when
        postService.write(post);

        // then
        Post savedPost = postRepository.findById(post.getId()).orElseThrow();
        assertThat(savedPost.getBoardId()).isEqualTo(boardId);
        assertThat(savedPost.getTitle()).isEqualTo(title);
        assertThat(savedPost.getContent()).isEqualTo(content);

        PostStat stat = statRepository.findById(post.getId()).orElseThrow();
        assertThat(stat.getPostId()).isEqualTo(post.getId());
        assertThat(stat.getViewCount()).isEqualTo(0);
        assertThat(stat.getLikeCount()).isEqualTo(0);
        assertThat(stat.getCommentCount()).isEqualTo(0);
    }

    @Test
    void 게시글_좋아요_성공() {
        // given
        Long boardId = 1L;
        Long categoryId = 2L;
        String title = "테스트 제목";
        String content = "테스트 내용";
        Long authorId = 100L; // 작성자
        Long userId = 101L; // 다른 사용자

        Post post = Post.write(boardId, categoryId, authorId, title, content);
        postService.write(post);

        // when
        postService.like(post, userId);

        // then
        List<PostLike> likes = likeRepository.findByPostIdAndType(post.getId(), LikeType.LIKE);
        assertThat(likes).hasSize(1);
        assertThat(likes.get(0).getPostId()).isEqualTo(post.getId());
        assertThat(likes.get(0).getUserId()).isEqualTo(userId);

        PostStat stat = statRepository.findById(post.getId()).orElseThrow();
        assertThat(stat.getLikeCount()).isEqualTo(1);
    }

    @Test
    void 게시글_싫어요_성공() {
        // given
        Long boardId = 1L;
        Long categoryId = 2L;
        String title = "테스트 제목";
        String content = "테스트 내용";
        Long authorId = 100L; // 작성자
        Long userId = 101L; // 다른 사용자

        Post post = Post.write(boardId, categoryId, authorId, title, content);
        postService.write(post);

        // when
        postService.dislike(post, userId);

        // then
        List<PostLike> dislikes = likeRepository.findByPostIdAndType(post.getId(), LikeType.DISLIKE);
        assertThat(dislikes).hasSize(1);
        assertThat(dislikes.get(0).getPostId()).isEqualTo(post.getId());
        assertThat(dislikes.get(0).getUserId()).isEqualTo(userId);

        PostStat stat = statRepository.findById(post.getId()).orElseThrow();
        assertThat(stat.getDislikeCount()).isEqualTo(1);
    }

    @Test
    void 게시글_중복_좋아요_방지() {
        // given
        Long boardId = 1L;
        Long categoryId = 2L;
        String title = "테스트 제목";
        String content = "테스트 내용";
        Long authorId = 100L; // 작성자
        Long userId = 101L; // 다른 사용자

        Post post = Post.write(boardId, categoryId, authorId, title, content);
        postService.write(post);

        postService.like(post, userId);

        assertThat(statRepository.findById(post.getId()).orElseThrow().getLikeCount()).isEqualTo(1);

        assertThatThrownBy(() -> postService.like(post, userId))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("이미 좋아요를 눌렀습니다.");

        assertThat(statRepository.findById(post.getId()).orElseThrow().getLikeCount()).isEqualTo(1);
    }

    @Test
    void 게시글_중복_싫어요_방지() {
        // given
        Long boardId = 1L;
        Long categoryId = 2L;
        String title = "테스트 제목";
        String content = "테스트 내용";
        Long authorId = 100L; // 작성자
        Long userId = 101L; // 다른 사용자

        Post post = Post.write(boardId, categoryId, authorId, title, content);
        postService.write(post);

        postService.dislike(post, userId);

        assertThat(statRepository.findById(post.getId()).orElseThrow().getDislikeCount()).isEqualTo(1);

        assertThatThrownBy(() -> postService.dislike(post, userId))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("이미 싫어요를 눌렀습니다.");

        assertThat(statRepository.findById(post.getId()).orElseThrow().getDislikeCount()).isEqualTo(1);
    }

    @Test
    void 게시글_좋아요_취소_성공() {
        // given
        Long boardId = 1L;
        Long categoryId = 2L;
        String title = "좋아요 취소 테스트";
        String content = "테스트 내용";
        Long authorId = 100L; // 작성자
        Long userId = 101L; // 다른 사용자

        Post post = Post.write(boardId, categoryId, authorId, title, content);
        postService.write(post);

        postService.like(post, userId);

        PostStat beforeStat = statRepository.findById(post.getId()).orElseThrow();
        assertThat(beforeStat.getLikeCount()).isEqualTo(1);

        // when
        postService.cancelLike(post, userId);

        // then
        Optional<PostLike> likeOpt = likeRepository.findByIdForUpdate(post.getId(), userId, LikeType.LIKE);
        assertThat(likeOpt.isEmpty());

        PostStat afterStat = statRepository.findById(post.getId()).orElseThrow();
        assertThat(afterStat.getLikeCount()).isEqualTo(0);
    }

    @Test
    void 게시글_싫어요_취소_성공() {
        // given
        Long boardId = 1L;
        Long categoryId = 2L;
        String title = "싫어요 취소 테스트";
        String content = "테스트 내용";
        Long authorId = 100L; // 작성자
        Long userId = 101L; // 다른 사용자

        Post post = Post.write(boardId, categoryId, authorId, title, content);
        postService.write(post);

        postService.dislike(post, userId);

        PostStat beforeStat = statRepository.findById(post.getId()).orElseThrow();
        assertThat(beforeStat.getDislikeCount()).isEqualTo(1);

        // when
        postService.cancelDislike(post, userId);

        // then
        Optional<PostLike> dislikeOpt = likeRepository.findByIdForUpdate(post.getId(), userId, LikeType.DISLIKE);
        assertThat(dislikeOpt.isEmpty());

        PostStat afterStat = statRepository.findById(post.getId()).orElseThrow();
        assertThat(afterStat.getDislikeCount()).isEqualTo(0);
    }

    @Test
    void 누르지_않은_좋아요_취소시_예외발생() {
        // given
        Long boardId = 1L;
        Long categoryId = 2L;
        String title = "좋아요 미누름 취소 테스트";
        String content = "테스트 내용";
        Long authorId = 100L; // 작성자
        Long userId = 101L; // 다른 사용자

        Post post = Post.write(boardId, categoryId, authorId, title, content);
        postService.write(post);

        // when/then
        assertThatThrownBy(() -> postService.cancelLike(post, userId))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("좋아요를 누르지 않았습니다.");
    }

    @Test
    void 누르지_않은_싫어요_취소시_예외발생() {
        // given
        Long boardId = 1L;
        Long categoryId = 2L;
        String title = "싫어요 미누름 취소 테스트";
        String content = "테스트 내용";
        Long authorId = 100L; // 작성자
        Long userId = 101L; // 다른 사용자

        Post post = Post.write(boardId, categoryId, authorId, title, content);
        postService.write(post);

        // when/then
        assertThatThrownBy(() -> postService.cancelDislike(post, userId))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("싫어요를 누르지 않았습니다.");
    }

    @Test
    void 여러사용자_좋아요_누를시_카운트_정확히_증가() {
        // given
        Long boardId = 1L;
        Long categoryId = 2L;
        String title = "다중 좋아요 테스트";
        String content = "테스트 내용";
        Long authorId = 100L; // 작성자
        int userCount = 5;

        Post post = Post.write(boardId, categoryId, authorId, title, content);
        postService.write(post);

        // when
        for (int i = 1; i <= userCount; i++) {
            Long userId = 200L + i;
            postService.like(post, userId);

            PostStat currentStat = statRepository.findById(post.getId()).orElseThrow();
            assertThat(currentStat.getLikeCount()).isEqualTo(i);
        }

        // then
        PostStat finalStat = statRepository.findById(post.getId()).orElseThrow();
        assertThat(finalStat.getLikeCount()).isEqualTo(userCount);

        List<PostLike> likes = likeRepository.findByPostIdAndType(post.getId(), LikeType.LIKE);
        assertThat(likes).hasSize(userCount);
    }
}