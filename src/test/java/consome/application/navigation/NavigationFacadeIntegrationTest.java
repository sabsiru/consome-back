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
        Board board1 = adminBoardFacade.create("게임게시판", "게임 관련", 1);
        Board board2 = adminBoardFacade.create("잡담게시판", "잡담용", 2);

        PostResult post1 = postFacade.post(PostCommand.of(board1.getId(), categoryId, userId, "게임글1", "내용1"));
        PostResult post2 = postFacade.post(PostCommand.of(board2.getId(), categoryId, userId, "잡담글1", "내용2"));

        // board1 게시글에 좋아요
        Post post = postFacade.getPost(post1.postId());
        postFacade.like(post, userId2);

        // when
        PopularBoardCriteria criteria = new PopularBoardCriteria(6, 5, 7, PopularityType.LIKE_COUNT);
        List<PopularBoardResult> results = navigationFacade.getPopularBoards(criteria);

        // then
        assertThat(results).isNotEmpty();
        assertThat(results.get(0).boardId()).isEqualTo(board1.getId());
    }

    @Test
    @DisplayName("인기 게시판 조회 - 댓글 수 기준 정렬")
    void getPopularBoards_sortByCommentCount() {
        // given
        Board board1 = adminBoardFacade.create("게임게시판", "게임 관련", 1);
        Board board2 = adminBoardFacade.create("잡담게시판", "잡담용", 2);

        PostResult post1 = postFacade.post(PostCommand.of(board1.getId(), categoryId, userId, "게임글1", "내용1"));
        PostResult post2 = postFacade.post(PostCommand.of(board2.getId(), categoryId, userId, "잡담글1", "내용2"));

        // board2 게시글에 댓글 작성
        commentFacade.comment(post2.postId(),  userId2,null ,"댓글1");
        commentFacade.comment(post2.postId(),  userId2, null,"댓글2");

        // when
        PopularBoardCriteria criteria = new PopularBoardCriteria(6, 5, 7, PopularityType.COMMENT_COUNT);
        List<PopularBoardResult> results = navigationFacade.getPopularBoards(criteria);

        // then
        assertThat(results).isNotEmpty();
        assertThat(results.get(0).boardId()).isEqualTo(board2.getId());
    }

    @Test
    @DisplayName("인기 게시판 조회 - 게시글 수 기준 정렬")
    void getPopularBoards_sortByPostCount() {
        // given
        Board board1 = adminBoardFacade.create("게임게시판", "게임 관련", 1);
        Board board2 = adminBoardFacade.create("잡담게시판", "잡담용", 2);

        postFacade.post(PostCommand.of(board1.getId(), categoryId, userId, "게임글1", "내용1"));
        postFacade.post(PostCommand.of(board2.getId(), categoryId, userId, "잡담글1", "내용2"));
        postFacade.post(PostCommand.of(board2.getId(), categoryId, userId, "잡담글2", "내용3"));
        postFacade.post(PostCommand.of(board2.getId(), categoryId, userId, "잡담글3", "내용4"));

        // when
        PopularBoardCriteria criteria = new PopularBoardCriteria(6, 5, 7, PopularityType.POST_COUNT);
        List<PopularBoardResult> results = navigationFacade.getPopularBoards(criteria);

        // then
        assertThat(results).isNotEmpty();
        assertThat(results.get(0).boardId()).isEqualTo(board2.getId());
    }

    @Test
    @DisplayName("인기 게시판 조회 - 게시글 없으면 빈 리스트")
    void getPopularBoards_emptyWhenNoPosts() {
        // given
        adminBoardFacade.create("빈게시판", "설명", 1);

        // when
        List<PopularBoardResult> results = navigationFacade.getPopularBoards(PopularBoardCriteria.defaults());

        // then
        assertThat(results).isEmpty();
    }

    @Test
    @DisplayName("인기 게시판 조회 - previewLimit 적용")
    void getPopularBoards_previewLimit() {
        // given
        Board board = adminBoardFacade.create("게임게시판", "게임 관련", 1);

        for (int i = 1; i <= 10; i++) {
            postFacade.post(PostCommand.of(board.getId(), categoryId, userId, "게시글" + i, "내용" + i));
        }

        // when
        PopularBoardCriteria criteria = new PopularBoardCriteria(6, 3, 7, PopularityType.POST_COUNT);
        List<PopularBoardResult> results = navigationFacade.getPopularBoards(criteria);

        // then
        assertThat(results).hasSize(1);
        assertThat(results.get(0).posts()).hasSize(3);
    }

}