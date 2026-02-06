package consome.application.board;

import consome.application.post.PostCommand;
import consome.application.post.PostFacade;
import consome.application.post.PostPagingResult;
import consome.application.post.PostResult;
import consome.application.user.UserFacade;
import consome.application.user.UserRegisterCommand;
import consome.domain.admin.Category;
import consome.domain.comment.CommentService;
import consome.domain.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.utility.TestcontainersConfiguration;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Import(TestcontainersConfiguration.class)
@Transactional
class BoardFacadeIntegrationTest {

    @Autowired
    private BoardFacade boardFacade;

    @Autowired
    private PostFacade postFacade;

    @Autowired
    private UserFacade userFacade;

    @Autowired
    private CommentService commentService;

    @Autowired
    private UserRepository userRepository;

    private Long userId;
    private final Long boardId = 1L;
    private final Long categoryId = 1L;
    private final Long categoryId2 = 2L;
    private final Pageable pageable = PageRequest.of(0, 100);

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
        UserRegisterCommand command = UserRegisterCommand.of("testuser", "테스트유저", "Password123");
        userId = userFacade.register(command);
    }

    @Nested
    @DisplayName("게시글 목록 조회")
    class GetPostsTest {

        @Test
        @DisplayName("게시판별 게시글 목록을 조회한다")
        void getPosts_success() {
            // given
            long initialCount = boardFacade.getPosts(boardId, pageable, null).totalElements();
            PostResult post1 = postFacade.post(PostCommand.of(boardId, categoryId, userId, "첫번째 글", "첫번째 내용"));
            PostResult post2 = postFacade.post(PostCommand.of(boardId, categoryId, userId, "두번째 글", "두번째 내용"));

            // when
            PostPagingResult result = boardFacade.getPosts(boardId, pageable, null);

            // then
            assertThat(result.boardId()).isEqualTo(boardId);
            assertThat(result.totalElements()).isEqualTo(initialCount + 2);
            assertThat(result.posts())
                    .extracting("postId")
                    .contains(post1.postId(), post2.postId());
        }

        @Test
        @DisplayName("카테고리별로 게시글을 필터링한다")
        void getPosts_withCategoryFilter() {
            // given
            long initialCount = boardFacade.getPosts(boardId, pageable, categoryId).totalElements();
            PostResult cat1Post = postFacade.post(PostCommand.of(boardId, categoryId, userId, "카테고리1 글", "내용"));
            postFacade.post(PostCommand.of(boardId, categoryId2, userId, "카테고리2 글", "내용"));

            // when
            PostPagingResult result = boardFacade.getPosts(boardId, pageable, categoryId);

            // then
            assertThat(result.totalElements()).isEqualTo(initialCount + 1);
            assertThat(result.posts())
                    .extracting("postId")
                    .contains(cat1Post.postId());
        }
    }

    @Nested
    @DisplayName("게시글 검색")
    class SearchPostsTest {

        @Test
        @DisplayName("제목으로 게시글을 검색한다")
        void searchPosts_byTitle() {
            // given
            String uniqueKeyword = "유니크키워드" + System.currentTimeMillis();
            PostResult target = postFacade.post(PostCommand.of(boardId, categoryId, userId, uniqueKeyword + " 강좌", "내용입니다"));
            postFacade.post(PostCommand.of(boardId, categoryId, userId, "자바 기초", "다른 내용"));

            // when
            PostPagingResult result = boardFacade.searchPosts(boardId, uniqueKeyword, "title", pageable);

            // then
            assertThat(result.posts())
                    .extracting("postId")
                    .containsExactly(target.postId());
        }

        @Test
        @DisplayName("내용으로 게시글을 검색한다")
        void searchPosts_byContent() {
            // given
            String uniqueKeyword = "유니크내용" + System.currentTimeMillis();
            PostResult target = postFacade.post(PostCommand.of(boardId, categoryId, userId, "제목1", uniqueKeyword + " 사용법"));
            postFacade.post(PostCommand.of(boardId, categoryId, userId, "제목2", "JPA 기초"));

            // when
            PostPagingResult result = boardFacade.searchPosts(boardId, uniqueKeyword, "content", pageable);

            // then
            assertThat(result.posts())
                    .extracting("postId")
                    .containsExactly(target.postId());
        }

        @Test
        @DisplayName("댓글 내용으로 게시글을 검색한다")
        void searchPosts_byComment() {
            // given
            String uniqueKeyword = "유니크댓글" + System.currentTimeMillis();
            PostResult target = postFacade.post(PostCommand.of(boardId, categoryId, userId, "제목1", "내용1"));
            PostResult other = postFacade.post(PostCommand.of(boardId, categoryId, userId, "제목2", "내용2"));
            commentService.comment(target.postId(), userId, null, uniqueKeyword + " 캐싱 전략");
            commentService.comment(other.postId(), userId, null, "일반 댓글");

            // when
            PostPagingResult result = boardFacade.searchPosts(boardId, uniqueKeyword, "comment", pageable);

            // then
            assertThat(result.posts())
                    .extracting("postId")
                    .containsExactly(target.postId());
        }

        @Test
        @DisplayName("전체 검색은 제목, 내용, 댓글을 모두 검색한다")
        void searchPosts_all() {
            // given
            String uniqueKeyword = "통합검색" + System.currentTimeMillis();
            PostResult postWithTitle = postFacade.post(PostCommand.of(boardId, categoryId, userId, uniqueKeyword + " 작성", "일반내용"));
            PostResult postWithContent = postFacade.post(PostCommand.of(boardId, categoryId, userId, "제목", uniqueKeyword + " 예제"));
            PostResult postWithComment = postFacade.post(PostCommand.of(boardId, categoryId, userId, "다른제목", "다른내용"));
            commentService.comment(postWithComment.postId(), userId, null, uniqueKeyword + " 관련 댓글");

            // when
            PostPagingResult result = boardFacade.searchPosts(boardId, uniqueKeyword, "all", pageable);

            // then
            assertThat(result.posts())
                    .extracting("postId")
                    .containsExactlyInAnyOrder(postWithTitle.postId(), postWithContent.postId(), postWithComment.postId());
        }

        @Test
        @DisplayName("검색 결과가 없으면 빈 목록을 반환한다")
        void searchPosts_noResult() {
            // given
            String nonExistKeyword = "절대없는키워드" + System.currentTimeMillis();

            // when
            PostPagingResult result = boardFacade.searchPosts(boardId, nonExistKeyword, "all", pageable);

            // then
            assertThat(result.posts()).isEmpty();
        }
    }

    @Nested
    @DisplayName("게시판/카테고리 조회")
    class BoardCategoryTest {

        @Test
        @DisplayName("게시판의 카테고리 목록을 조회한다")
        void getCategories_success() {
            // when
            List<Category> categories = boardFacade.getCategories(boardId);

            // then
            assertThat(categories).isNotEmpty();
        }

        @Test
        @DisplayName("게시판 이름을 조회한다")
        void findNameById_success() {
            // when
            String boardName = boardFacade.findNameById(boardId);

            // then
            assertThat(boardName).isNotBlank();
        }
    }
}
