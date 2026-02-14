package consome.application.post;

import org.testcontainers.utility.TestcontainersConfiguration;
import consome.application.comment.CommentFacade;
import consome.application.user.UserFacade;
import consome.application.user.UserRegisterCommand;
import consome.domain.post.PostService;
import consome.domain.post.entity.Post;
import consome.domain.post.entity.PostStat;
import consome.domain.post.exception.PostException;
import consome.domain.post.repository.PostStatRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Import(TestcontainersConfiguration.class)
@ActiveProfiles("test")
class PostConcurrencyTest {

    @Autowired UserFacade userFacade;
    @Autowired PostFacade postFacade;
    @Autowired CommentFacade commentFacade;
    @Autowired PostService postService;
    @Autowired PostStatRepository statRepository;

    private static final int THREAD_COUNT = 10;
    private static final long BOARD_ID = 1L;
    private static final long CATEGORY_ID = 1L;

    private List<Long> userIds;
    private Post post;

    @BeforeEach
    void setUp() {
        userIds = new ArrayList<>();
        String suffix = UUID.randomUUID().toString().substring(0, 8);
        for (int i = 0; i < THREAD_COUNT; i++) {
            Long userId = userFacade.register(
                UserRegisterCommand.of("user" + suffix + i, "닉" + suffix + i, "Password123!")
            );
            userIds.add(userId);
        }

        Long authorId = userIds.get(0);
        PostResult postResult = postFacade.post(PostCommand.of(BOARD_ID, CATEGORY_ID, authorId, "제목", "내용"));
        post = postService.getPost(postResult.postId());
    }

    @Test
    void 동시_좋아요_요청시_카운트_정확성_검증() throws InterruptedException {
        ExecutorService executor = Executors.newFixedThreadPool(THREAD_COUNT);
        CountDownLatch latch = new CountDownLatch(THREAD_COUNT);

        for (int i = 0; i < THREAD_COUNT; i++) {
            int idx = i;
            executor.submit(() -> {
                try {
                    postService.like(post, userIds.get(idx));
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        executor.shutdown();
        executor.awaitTermination(10, TimeUnit.SECONDS);

        PostStat stat = statRepository.findById(post.getId()).orElseThrow();
        assertThat(stat.getLikeCount()).isEqualTo(THREAD_COUNT);
    }

    @Test
    void 동시_싫어요_요청시_카운트_정확성_검증() throws InterruptedException {
        ExecutorService executor = Executors.newFixedThreadPool(THREAD_COUNT);
        CountDownLatch latch = new CountDownLatch(THREAD_COUNT);

        for (int i = 0; i < THREAD_COUNT; i++) {
            int idx = i;
            executor.submit(() -> {
                try {
                    postService.dislike(post, userIds.get(idx));
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        executor.shutdown();
        executor.awaitTermination(10, TimeUnit.SECONDS);

        PostStat stat = statRepository.findById(post.getId()).orElseThrow();
        assertThat(stat.getDislikeCount()).isEqualTo(THREAD_COUNT);
    }

    @Test
    void 동시_댓글_작성시_카운트_정확성_검증() throws InterruptedException {
        ExecutorService executor = Executors.newFixedThreadPool(THREAD_COUNT);
        CountDownLatch latch = new CountDownLatch(THREAD_COUNT);

        for (int i = 0; i < THREAD_COUNT; i++) {
            int idx = i;
            executor.submit(() -> {
                try {
                    commentFacade.comment(post.getId(), userIds.get(idx), null, "댓글내용" + idx);
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        executor.shutdown();
        executor.awaitTermination(10, TimeUnit.SECONDS);

        PostStat stat = statRepository.findById(post.getId()).orElseThrow();
        assertThat(stat.getCommentCount()).isEqualTo(THREAD_COUNT);
    }

    @Test
    void 동일_사용자_동시_좋아요_요청시_1회만_적용() throws InterruptedException {
        Long singleUserId = userIds.get(0);
        ExecutorService executor = Executors.newFixedThreadPool(THREAD_COUNT);
        CountDownLatch latch = new CountDownLatch(THREAD_COUNT);
        AtomicInteger exceptionCount = new AtomicInteger(0);

        for (int i = 0; i < THREAD_COUNT; i++) {
            executor.submit(() -> {
                try {
                    postService.like(post, singleUserId);
                } catch (PostException e) {
                    exceptionCount.incrementAndGet();
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        executor.shutdown();
        executor.awaitTermination(10, TimeUnit.SECONDS);

        PostStat stat = statRepository.findById(post.getId()).orElseThrow();
        assertThat(stat.getLikeCount()).isEqualTo(1);
        assertThat(exceptionCount.get()).isEqualTo(THREAD_COUNT - 1);
    }

    @Test
    void 동일_사용자_동시_싫어요_요청시_1회만_적용() throws InterruptedException {
        Long singleUserId = userIds.get(0);
        ExecutorService executor = Executors.newFixedThreadPool(THREAD_COUNT);
        CountDownLatch latch = new CountDownLatch(THREAD_COUNT);
        AtomicInteger exceptionCount = new AtomicInteger(0);

        for (int i = 0; i < THREAD_COUNT; i++) {
            executor.submit(() -> {
                try {
                    postService.dislike(post, singleUserId);
                } catch (PostException e) {
                    exceptionCount.incrementAndGet();
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        executor.shutdown();
        executor.awaitTermination(10, TimeUnit.SECONDS);

        PostStat stat = statRepository.findById(post.getId()).orElseThrow();
        assertThat(stat.getDislikeCount()).isEqualTo(1);
        assertThat(exceptionCount.get()).isEqualTo(THREAD_COUNT - 1);
    }
}
