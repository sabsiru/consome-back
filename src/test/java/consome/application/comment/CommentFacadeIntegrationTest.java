package consome.application.comment;

import consome.application.user.UserFacade;
import consome.application.user.UserRegisterCommand;
import consome.domain.comment.CommentService;
import consome.domain.post.PostService;
import consome.domain.post.entity.Post;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.utility.TestcontainersConfiguration;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@SpringBootTest
@Transactional
@Import(TestcontainersConfiguration.class)
class CommentFacadeIntegrationTest {

    @Autowired
    private CommentFacade commentFacade;

    @Autowired
    private CommentService commentService;

    @Autowired
    private PostService postService;

    @Autowired
    private UserFacade userFacade;


    @Test
    void 댓글_페이징_통합_테스트() {
        // given
        UserRegisterCommand registerCommand = new UserRegisterCommand(
                "testerID",
                "테스터",
                "Password123"
        );
        Long register = userFacade.register(registerCommand);
        Post post = postService.post(1L, 2L, register, "testTitle",
                "testContent");

        for (int i = 1; i <= 100; i++) {
            commentService.comment(post.getId(), register, null, "댓글 " +
                    i);
        }

        // when
        Page<CommentListResult> firstPage = commentFacade.listByPost(
                post.getId(),
                null,  // userId 없이 조회
                PageRequest.of(0, 50)
        );

        Page<CommentListResult> secondPage = commentFacade.listByPost(
                post.getId(),
                null,
                PageRequest.of(1, 50)
        );

        // then
        assertThat(firstPage.getContent()).hasSize(50);
        assertThat(secondPage.getContent()).hasSize(50);
        assertThat(firstPage.getTotalElements()).isEqualTo(100);
    }

    @Test
    void 댓글_추천_상태_조회_테스트() {
        // given
        UserRegisterCommand registerCommand = new UserRegisterCommand(
                "testerID",
                "테스터",
                "Password123"
        );
        Long register = userFacade.register(registerCommand);
        Post post = postService.post(1L, 2L, register, "testTitle",
                "testContent");
        CommentResult comment = commentFacade.comment(post.getId(), register,
                null, "테스트 댓글");

        // 댓글 추천
        commentFacade.like(comment.commentId(), register);

        // when
        Page<CommentListResult> page = commentFacade.listByPost(
                post.getId(),
                register,  // userId로 조회
                PageRequest.of(0, 50)
        );

        // then
        CommentListResult result = page.getContent().get(0);
        assertThat(result.hasLiked()).isTrue();
        assertThat(result.hasDisliked()).isFalse();
    }
}