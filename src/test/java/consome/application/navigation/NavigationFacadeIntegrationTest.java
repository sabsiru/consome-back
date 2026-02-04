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
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Import;
import org.testcontainers.utility.TestcontainersConfiguration;

import java.util.List;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@SpringBootTest
@Transactional
@Import(TestcontainersConfiguration.class)
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

    private Long userId;
    private Long userId2;
    private Long categoryId = 1L;

    @BeforeEach
    void setUp() {
        userId = userFacade.register(UserRegisterCommand.of("testuser1", "닉네임1", "Password123"));
        userId2 = userFacade.register(UserRegisterCommand.of("testuser2", "닉네임2", "Password123"));
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
    @DisplayName("인기 게시글 조회 - 기본")
    void getPopularPosts_default() {
        // given
        Board board = adminBoardFacade.create("테스트게시판", "설명", 91);
        PostResult post = postFacade.post(PostCommand.of(board.getId(), categoryId, userId, "인기글", "내용"));

        // 조회수 증가 (minViews 충족) - 각기 다른 IP와 null userId로 호출
        for (int i = 0; i < 60; i++) {
            postFacade.increaseViewCount(post.postId(), "192.168." + i + ".1", null);
        }

        // when
        List<PopularPostResult> results = navigationFacade.getPopularPosts(PopularPostCriteria.defaults());

        // then
        assertThat(results.stream().anyMatch(r -> r.postId().equals(post.postId()))).isTrue();
    }

    @Test
    @DisplayName("인기 게시글 조회 - 최소 조회수 미달 제외")
    void getPopularPosts_excludeBelowMinViews() {
        // given
        Board board = adminBoardFacade.create("테스트게시판", "설명", 91);
        PostResult post = postFacade.post(PostCommand.of(board.getId(), categoryId, userId, "조회수적은글", "내용"));

        // 조회수 10 (minViews 50 미달)
        for (int i = 0; i < 10; i++) {
            postFacade.increaseViewCount(post.postId(), "192.168.0." + i, userId2);
        }

        // when
        PopularPostCriteria criteria = new PopularPostCriteria(100, 7, 50);
        List<PopularPostResult> results = navigationFacade.getPopularPosts(criteria);

        // then
        assertThat(results.stream().noneMatch(r -> r.postId().equals(post.postId()))).isTrue();
    }

    @Test
    @DisplayName("인기 게시글 조회 - Wilson Score 정렬")
    void getPopularPosts_sortedByWilsonScore() {
        // given
        Board board = adminBoardFacade.create("테스트게시판", "설명", 91);

        // 게시글1: 조회 100, 좋아요 20 (참여율 20%)
        PostResult post1 = postFacade.post(PostCommand.of(board.getId(), categoryId, userId, "참여율높은글", "내용"));
        for (int i = 0; i < 100; i++) {
            postFacade.increaseViewCount(post1.postId(), "10.0." + i + ".1", null);
        }
        Post postEntity1 = postFacade.getPost(post1.postId());
        for (int i = 0; i < 20; i++) {
            Long tempUserId = userFacade.register(UserRegisterCommand.of("temp" + i, "임시" + i, "Password123"));
            postFacade.like(postEntity1, tempUserId);
        }

        // 게시글2: 조회 100, 좋아요 5 (참여율 5%)
        PostResult post2 = postFacade.post(PostCommand.of(board.getId(), categoryId, userId, "참여율낮은글", "내용"));
        for (int i = 0; i < 100; i++) {
            postFacade.increaseViewCount(post2.postId(), "20.0." + i + ".1", null);
        }
        Post postEntity2 = postFacade.getPost(post2.postId());
        for (int i = 0; i < 5; i++) {
            Long tempUserId = userFacade.register(UserRegisterCommand.of("tempB" + i, "임시B" + i, "Password123"));
            postFacade.like(postEntity2, tempUserId);
        }

        // when
        PopularPostCriteria criteria = new PopularPostCriteria(100, 7, 50);
        List<PopularPostResult> results = navigationFacade.getPopularPosts(criteria);

        // then
        PopularPostResult result1 = results.stream().filter(r -> r.postId().equals(post1.postId())).findFirst().orElseThrow();
        PopularPostResult result2 = results.stream().filter(r -> r.postId().equals(post2.postId())).findFirst().orElseThrow();
        assertThat(result1.score()).isGreaterThan(result2.score());
    }

    @Test
    @DisplayName("인기 게시글 조회 - 댓글 가중치 반영")
    void getPopularPosts_commentWeightApplied() {
        // given
        Board board = adminBoardFacade.create("테스트게시판", "설명", 91);

        // post1: 조회 100, 좋아요 10, 댓글 0 → positive = 10
        PostResult post1 = postFacade.post(PostCommand.of(board.getId(), categoryId, userId,
                "좋아요만", "내용"));
        for (int i = 0; i < 100; i++) {
            postFacade.increaseViewCount(post1.postId(), "10.0." + i + ".1", null);
        }
        Post postEntity1 = postFacade.getPost(post1.postId());
        for (int i = 0; i < 10; i++) {
            Long tempUserId = userFacade.register(UserRegisterCommand.of("likeUser" + i,
                    "좋아요유저" + i, "Password123"));
            postFacade.like(postEntity1, tempUserId);
        }

        // post2: 조회 100, 좋아요 5, 댓글 17 → positive = 5 + (17 * 0.3) = 10.1
        PostResult post2 = postFacade.post(PostCommand.of(board.getId(), categoryId, userId,
                "댓글많음", "내용"));
        for (int i = 0; i < 100; i++) {
            postFacade.increaseViewCount(post2.postId(), "20.0." + i + ".1", null);
        }
        Post postEntity2 = postFacade.getPost(post2.postId());
        for (int i = 0; i < 5; i++) {
            Long tempUserId = userFacade.register(UserRegisterCommand.of("likeUserB" + i,
                    "좋아요유저B" + i, "Password123"));
            postFacade.like(postEntity2, tempUserId);
        }
        for (int i = 0; i < 17; i++) {
            Long tempUserId = userFacade.register(UserRegisterCommand.of("commentUser" + i,
                    "댓글유저" + i, "Password123"));
            commentFacade.comment(post2.postId(), tempUserId, null, "댓글" + i);
        }

        // when
        PopularPostCriteria criteria = new PopularPostCriteria(100, 7, 50);
        List<PopularPostResult> results = navigationFacade.getPopularPosts(criteria);

        // then
        PopularPostResult result1 = results.stream().filter(r ->
                r.postId().equals(post1.postId())).findFirst().orElseThrow();
        PopularPostResult result2 = results.stream().filter(r ->
                r.postId().equals(post2.postId())).findFirst().orElseThrow();
        assertThat(result2.score()).isGreaterThan(result1.score());
    }

}