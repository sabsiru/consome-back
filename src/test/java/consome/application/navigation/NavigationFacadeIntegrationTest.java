package consome.application.navigation;

import consome.application.admin.AdminBoardFacade;
import consome.application.comment.CommentFacade;
import consome.application.post.PostCommand;
import consome.application.post.PostFacade;
import consome.application.post.PostResult;
import consome.application.user.UserFacade;
import consome.application.user.UserRegisterCommand;
import consome.domain.admin.Board;
import consome.domain.post.PopularityType;
import consome.domain.post.entity.Post;
import consome.domain.post.repository.PopularPostRepository;
import consome.infrastructure.redis.PopularPostRedisRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.utility.TestcontainersConfiguration;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@SpringBootTest
@Transactional
@Import(TestcontainersConfiguration.class)
@ActiveProfiles("test")
class NavigationFacadeIntegrationTest {

    @Autowired
    NavigationFacade navigationFacade;
    @Autowired
    AdminBoardFacade adminBoardFacade;
    @Autowired
    UserFacade userFacade;
    @Autowired
    PostFacade postFacade;
    @Autowired
    CommentFacade commentFacade;
    @Autowired
    CacheManager cacheManager;
    @Autowired
    PopularPostRedisRepository popularPostRedisRepository;
    @Autowired
    PopularPostRepository popularPostRepository;

    private Long userId;
    private Long userId2;
    private Long categoryId = 1L;

    @BeforeEach
    void setUp() {
        String suffix = UUID.randomUUID().toString().replace("-", "").substring(0, 8);
        userId = userFacade.register(UserRegisterCommand.of("navtest" + suffix, "네비닉" + suffix, "Password123"));
        userId2 = userFacade.register(UserRegisterCommand.of("navtest2" + suffix, "네비닉2" + suffix, "Password123"));
        cacheManager.getCacheNames().forEach(name ->
                cacheManager.getCache(name).clear());
    }

    @Test
    @DisplayName("인기 게시판 조회 - 기본")
    void getPopularBoards_default() {
        // given
        Board board1 = adminBoardFacade.create("게임게시판", "게임 관련", 1);
        Board board2 = adminBoardFacade.create("잡담게시판", "잡담용", 2);

        postFacade.post(PostCommand.of(board1.getId(), categoryId, userId, "게임글1", "내용1"));
        postFacade.post(PostCommand.of(board2.getId(), categoryId, userId, "잡담글1", "내용2"));

        // when
        List<PopularBoardResult> results = navigationFacade.getPopularBoards(PopularBoardCriteria.defaults());

        // then
        assertThat(results).isNotEmpty();
        assertThat(results.get(0).boardId()).isNotNull();
        assertThat(results.get(0).boardName()).isNotNull();
        assertThat(results.get(0).score()).isNotNull();
        assertThat(results.get(0).posts()).isNotNull();
    }

    @Test
    @DisplayName("인기 게시판 조회 - 조회수 기준 정렬")
    void getPopularBoards_sortByViewCount() {
        // given
        Board board1 = adminBoardFacade.create("게임게시판", "게임 관련", 1);
        Board board2 = adminBoardFacade.create("잡담게시판", "잡담용", 2);

        PostResult post1 = postFacade.post(PostCommand.of(board1.getId(), categoryId, userId, "게임글1", "내용1"));
        PostResult post2 = postFacade.post(PostCommand.of(board2.getId(), categoryId, userId, "잡담글1", "내용2"));

        // board2 게시글에 조회수 증가
        postFacade.increaseViewCount(post2.postId(), "192.168.0.1", userId2);
        postFacade.increaseViewCount(post2.postId(), "192.168.0.2", categoryId);
        postFacade.increaseViewCount(post2.postId(), "192.168.0.3", categoryId);

        // when
        PopularBoardCriteria criteria = new PopularBoardCriteria(6, 5, 7, PopularityType.VIEW_COUNT);
        List<PopularBoardResult> results = navigationFacade.getPopularBoards(criteria);

        // then
        assertThat(results).isNotEmpty();
        assertThat(results.get(0).boardId()).isEqualTo(board2.getId());
    }

    @Test
    @DisplayName("인기 게시판 조회 - 좋아요 기준 정렬")
    void getPopularBoards_sortByLikeCount() {
        // given
        Board board1 = adminBoardFacade.create("게임게시판", "게임 관련", 91);
        Board board2 = adminBoardFacade.create("잡담게시판", "잡담용", 92);

        PostResult post1 = postFacade.post(PostCommand.of(board1.getId(), categoryId, userId, "게임글1", "내용1"));
        PostResult post2 = postFacade.post(PostCommand.of(board2.getId(), categoryId, userId, "잡담글1", "내용2"));

        // board1 게시글에 좋아요
        Post post = postFacade.getPost(post1.postId());
        postFacade.like(post, userId2);

        // when
        PopularBoardCriteria criteria = new PopularBoardCriteria(100, 5, 7, PopularityType.LIKE_COUNT);
        List<PopularBoardResult> results = navigationFacade.getPopularBoards(criteria);

        // then
        PopularBoardResult result1 = results.stream().filter(r -> r.boardId().equals(board1.getId())).findFirst().orElseThrow();
        PopularBoardResult result2 = results.stream().filter(r -> r.boardId().equals(board2.getId())).findFirst().orElseThrow();
        assertThat(result1.score()).isGreaterThan(result2.score());
    }

    @Test
    @DisplayName("인기 게시판 조회 - 댓글 수 기준 정렬")
    void getPopularBoards_sortByCommentCount() {
        // given
        Board board1 = adminBoardFacade.create("게임게시판", "게임 관련", 91);
        Board board2 = adminBoardFacade.create("잡담게시판", "잡담용", 92);

        PostResult post1 = postFacade.post(PostCommand.of(board1.getId(), categoryId, userId, "게임글1", "내용1"));
        PostResult post2 = postFacade.post(PostCommand.of(board2.getId(), categoryId, userId, "잡담글1", "내용2"));

        // board2 게시글에 댓글 작성
        commentFacade.comment(post2.postId(), userId2, null, "댓글1");
        commentFacade.comment(post2.postId(), userId2, null, "댓글2");

        // when
        PopularBoardCriteria criteria = new PopularBoardCriteria(100, 5, 7, PopularityType.COMMENT_COUNT);
        List<PopularBoardResult> results = navigationFacade.getPopularBoards(criteria);

        // then
        PopularBoardResult result1 = results.stream().filter(r -> r.boardId().equals(board1.getId())).findFirst().orElseThrow();
        PopularBoardResult result2 = results.stream().filter(r -> r.boardId().equals(board2.getId())).findFirst().orElseThrow();
        assertThat(result2.score()).isGreaterThan(result1.score());
    }

    @Test
    @DisplayName("인기 게시판 조회 - 게시글 수 기준 정렬")
    void getPopularBoards_sortByPostCount() {
        // given
        Board board1 = adminBoardFacade.create("게임게시판", "게임 관련", 91);
        Board board2 = adminBoardFacade.create("잡담게시판", "잡담용", 92);

        postFacade.post(PostCommand.of(board1.getId(), categoryId, userId, "게임글1", "내용1"));
        postFacade.post(PostCommand.of(board2.getId(), categoryId, userId, "잡담글1", "내용2"));
        postFacade.post(PostCommand.of(board2.getId(), categoryId, userId, "잡담글2", "내용3"));
        postFacade.post(PostCommand.of(board2.getId(), categoryId, userId, "잡담글3", "내용4"));

        // when
        PopularBoardCriteria criteria = new PopularBoardCriteria(100, 5, 7, PopularityType.POST_COUNT);
        List<PopularBoardResult> results = navigationFacade.getPopularBoards(criteria);

        // then
        PopularBoardResult result1 = results.stream().filter(r -> r.boardId().equals(board1.getId())).findFirst().orElseThrow();
        PopularBoardResult result2 = results.stream().filter(r -> r.boardId().equals(board2.getId())).findFirst().orElseThrow();
        assertThat(result2.score()).isGreaterThan(result1.score());
    }

    @Test
    @DisplayName("인기 게시판 조회 - 게시글 없는 게시판은 제외")
    void getPopularBoards_excludeBoardWithNoPosts() {
        // given
        Board emptyBoard = adminBoardFacade.create("빈게시판", "설명", 99);

        // when
        List<PopularBoardResult> results = navigationFacade.getPopularBoards(PopularBoardCriteria.defaults());

        // then
        assertThat(results).noneMatch(r -> r.boardId().equals(emptyBoard.getId()));
    }

    @Test
    @DisplayName("인기 게시판 조회 - previewLimit 적용")
    void getPopularBoards_previewLimit() {
        // given
        Board board = adminBoardFacade.create("게임게시판", "게임 관련", 91);

        for (int i = 1; i <= 10; i++) {
            postFacade.post(PostCommand.of(board.getId(), categoryId, userId, "게시글" + i, "내용" + i));
        }

        // when
        PopularBoardCriteria criteria = new PopularBoardCriteria(100, 3, 7, PopularityType.POST_COUNT);
        List<PopularBoardResult> results = navigationFacade.getPopularBoards(criteria);

        // then
        PopularBoardResult result = results.stream().filter(r -> r.boardId().equals(board.getId())).findFirst().orElseThrow();
        assertThat(result.posts()).hasSize(3);
    }

    @Test
    @DisplayName("인기 게시글 - 추천 시 Redis에 점수 저장")
    void popularPost_likeAddsScoreToRedis() {
        // given
        Board board = adminBoardFacade.create("테스트게시판", "설명", 91);
        PostResult post = postFacade.post(PostCommand.of(board.getId(), categoryId, userId, "테스트글", "내용"));
        Post postEntity = postFacade.getPost(post.postId());

        // when
        postFacade.like(postEntity, userId2);

        // then
        Double score = popularPostRedisRepository.getScore(post.postId());
        assertThat(score).isNotNull();
    }

    @Test
    @DisplayName("인기 게시글 - 조회 시 Redis에 점수 저장")
    void popularPost_viewAddsScoreToRedis() {
        // given
        Board board = adminBoardFacade.create("테스트게시판", "설명", 91);
        PostResult post = postFacade.post(PostCommand.of(board.getId(), categoryId, userId, "테스트글", "내용"));

        // when
        postFacade.increaseViewCount(post.postId(), "192.168.0.1", userId2);

        // then
        Double score = popularPostRedisRepository.getScore(post.postId());
        assertThat(score).isNotNull();
    }

    @Test
    @DisplayName("인기 게시글 - 댓글 시 Redis에 점수 저장")
    void popularPost_commentAddsScoreToRedis() {
        // given
        Board board = adminBoardFacade.create("테스트게시판", "설명", 91);
        PostResult post = postFacade.post(PostCommand.of(board.getId(), categoryId, userId, "테스트글", "내용"));

        // when
        commentFacade.comment(post.postId(), userId2, null, "댓글 내용");

        // then
        Double score = popularPostRedisRepository.getScore(post.postId());
        assertThat(score).isNotNull();
    }

    @Test
    @DisplayName("인기 게시글 - 임계값 미달 시 Redis에만 존재")
    void popularPost_belowThresholdStaysInRedis() {
        // given - 평균값 높게 설정하여 상대 점수가 낮도록
        Board board = adminBoardFacade.create("테스트게시판", "설명", 91);
        board.updateStats(100.0, 100.0, 100.0);
        PostResult post = postFacade.post(PostCommand.of(board.getId(), categoryId, userId, "일반글", "내용"));
        Post postEntity = postFacade.getPost(post.postId());

        // when - 추천 1회 (score < 1.0)
        postFacade.like(postEntity, userId2);

        // then - Redis에만 존재, DB에는 없음
        Double redisScore = popularPostRedisRepository.getScore(post.postId());
        boolean existsInDb = popularPostRepository.existsByPostId(post.postId());

        assertThat(redisScore).isNotNull();
        assertThat(redisScore).isLessThan(1.0);
        assertThat(existsInDb).isFalse();
    }

    @Test
    @Disabled("Redis Testcontainer 필요 - Mock으로는 정확한 동작 테스트 불가")
    @DisplayName("인기 게시글 - 임계값 도달 시 DB 적재 및 Redis 삭제")
    void popularPost_thresholdReachedSavesToDbAndRemovesFromRedis() {
        // given - 평균값 1로 설정하여 상대 점수가 높도록
        String suffix = UUID.randomUUID().toString().replace("-", "").substring(0, 8);
        Board board = adminBoardFacade.create("테스트게시판", "설명", 91);
        board.updateStats(1.0, 1.0, 1.0);
        PostResult post = postFacade.post(PostCommand.of(board.getId(), categoryId, userId, "인기글", "내용"));
        Post postEntity = postFacade.getPost(post.postId());

        // when - 추천 6회 (score >= 1.0 && likeCount >= 5)
        for (int i = 0; i < 6; i++) {
            Long tempUserId = userFacade.register(UserRegisterCommand.of("poplike" + suffix + i, "추천닉" + suffix + i, "Password123"));
            postFacade.like(postEntity, tempUserId);
        }

        // then
        boolean existsInDb = popularPostRepository.existsByPostId(post.postId());
        Double redisScore = popularPostRedisRepository.getScore(post.postId());

        assertThat(existsInDb).isTrue();
        assertThat(redisScore).isNull();
    }

    @Test
    @Disabled("Redis Testcontainer 필요 - Mock으로는 정확한 동작 테스트 불가")
    @DisplayName("인기 게시글 - 이미 등록된 게시글은 중복 처리 안함")
    void popularPost_alreadyRegisteredSkipped() {
        // given
        String suffix = UUID.randomUUID().toString().replace("-", "").substring(0, 8);
        Board board = adminBoardFacade.create("테스트게시판", "설명", 91);
        board.updateStats(1.0, 1.0, 1.0);
        PostResult post = postFacade.post(PostCommand.of(board.getId(), categoryId, userId, "인기글", "내용"));
        Post postEntity = postFacade.getPost(post.postId());

        // 인기 게시글로 등록
        for (int i = 0; i < 6; i++) {
            Long tempUserId = userFacade.register(UserRegisterCommand.of("first" + suffix + i, "첫번째" + suffix + i, "Password123"));
            postFacade.like(postEntity, tempUserId);
        }
        assertThat(popularPostRepository.existsByPostId(post.postId())).isTrue();

        // when - 추가 추천
        Long extraUser = userFacade.register(UserRegisterCommand.of("extra" + suffix, "추가" + suffix, "Password123"));
        postFacade.like(postEntity, extraUser);

        // then - Redis에 다시 추가되지 않음
        Double redisScore = popularPostRedisRepository.getScore(post.postId());
        assertThat(redisScore).isNull();
    }

    @Test
    @DisplayName("인기 게시글 - DB에서 조회")
    void popularPost_fetchFromDb() {
        // given
        String suffix = UUID.randomUUID().toString().replace("-", "").substring(0, 8);
        Board board = adminBoardFacade.create("테스트게시판", "설명", 91);
        board.updateStats(1.0, 1.0, 1.0);
        PostResult post = postFacade.post(PostCommand.of(board.getId(), categoryId, userId, "인기글", "내용"));
        Post postEntity = postFacade.getPost(post.postId());

        for (int i = 0; i < 6; i++) {
            Long tempUserId = userFacade.register(UserRegisterCommand.of("popfetch" + suffix + i, "조회닉" + suffix + i, "Password123"));
            postFacade.like(postEntity, tempUserId);
        }

        // when
        List<PopularPostResult> results = navigationFacade.getPopularPosts(new PopularPostCriteria(20));

        // then
        assertThat(results.stream().anyMatch(r -> r.postId().equals(post.postId()))).isTrue();
    }

    @Test
    @Disabled("Redis Testcontainer 필요 - Mock으로는 정확한 동작 테스트 불가")
    @DisplayName("인기 게시글 - 점수 계산 검증 (추천 가중치 0.7)")
    void popularPost_scoreCalculation() {
        // given - 평균값 1로 설정
        Board board = adminBoardFacade.create("테스트게시판", "설명", 91);
        board.updateStats(1.0, 1.0, 1.0);
        PostResult post = postFacade.post(PostCommand.of(board.getId(), categoryId, userId, "테스트글", "내용"));
        Post postEntity = postFacade.getPost(post.postId());

        // when - 추천 1회 (relativeLike = 1, score = 1 * 0.7 = 0.7)
        postFacade.like(postEntity, userId2);

        // then
        Double score = popularPostRedisRepository.getScore(post.postId());
        assertThat(score).isNotNull();
        assertThat(score).isGreaterThan(0.6);
        assertThat(score).isLessThan(0.8);
    }

}