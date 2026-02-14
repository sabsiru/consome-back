package consome.application.point;

import consome.application.user.UserFacade;
import consome.application.user.UserRegisterCommand;
import consome.domain.point.Point;
import consome.domain.point.PointHistoryType;
import consome.domain.point.PointService;
import consome.domain.point.repository.PointRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.utility.TestcontainersConfiguration;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Import(TestcontainersConfiguration.class)
@ActiveProfiles("test")
class PointConcurrencyTest {

    @Autowired UserFacade userFacade;
    @Autowired PointService pointService;
    @Autowired PointRepository pointRepository;

    private static final int THREAD_COUNT = 10;

    private Long userId;
    private int initialPoint;

    @BeforeEach
    void setUp() {
        String suffix = UUID.randomUUID().toString().substring(0, 8);
        userId = userFacade.register(
            UserRegisterCommand.of("user" + suffix, "닉" + suffix, "Password123!")
        );
        initialPoint = pointService.getCurrentPoint(userId);
    }

    @Test
    void 동시_포인트_적립시_정확성_검증() throws InterruptedException {
        ExecutorService executor = Executors.newFixedThreadPool(THREAD_COUNT);
        CountDownLatch latch = new CountDownLatch(THREAD_COUNT);

        int earnPointPerRequest = PointHistoryType.POST_LIKE.getPoint();

        for (int i = 0; i < THREAD_COUNT; i++) {
            executor.submit(() -> {
                try {
                    pointService.earn(userId, PointHistoryType.POST_LIKE);
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        executor.shutdown();
        executor.awaitTermination(10, TimeUnit.SECONDS);

        Point point = pointRepository.findByUserId(userId).orElseThrow();
        int expectedPoint = initialPoint + (earnPointPerRequest * THREAD_COUNT);
        assertThat(point.getUserPoint()).isEqualTo(expectedPoint);
    }

    @Test
    void 동시_포인트_차감시_정확성_검증() throws InterruptedException {
        // 충분한 포인트 확보
        for (int i = 0; i < THREAD_COUNT; i++) {
            pointService.earn(userId, PointHistoryType.POST_WRITE);
        }
        int pointAfterEarn = pointService.getCurrentPoint(userId);

        ExecutorService executor = Executors.newFixedThreadPool(THREAD_COUNT);
        CountDownLatch latch = new CountDownLatch(THREAD_COUNT);

        int penaltyPointPerRequest = PointHistoryType.POST_DISLIKE.getPoint();

        for (int i = 0; i < THREAD_COUNT; i++) {
            executor.submit(() -> {
                try {
                    pointService.penalize(userId, PointHistoryType.POST_DISLIKE);
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        executor.shutdown();
        executor.awaitTermination(10, TimeUnit.SECONDS);

        Point point = pointRepository.findByUserId(userId).orElseThrow();
        int expectedPoint = pointAfterEarn - (penaltyPointPerRequest * THREAD_COUNT);
        assertThat(point.getUserPoint()).isEqualTo(expectedPoint);
    }
}
