package consome.application.comment;

import consome.domain.comment.Comment;
import consome.domain.comment.CommentService;
import consome.domain.post.PostService;
import consome.domain.post.entity.Post;
import consome.domain.user.User;
import consome.domain.user.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@SpringBootTest
@Transactional
class CommentFacadeIntegrationTest {

    @Autowired
    private CommentFacade commentFacade;

    @Autowired
    private CommentService commentService;

    @Autowired
    private PostService postService;

    @Autowired
    private UserService userService;

    @Test
    void 댓글_페이징_통합_테스트() {
        // given
        User register = userService.register("testerID", "테스터", "Password123");
        Post post = postService.post(1L, 2L, register.getId(), "testTitle.", "testContent.");

        for (int i = 1; i <= 100; i++) {
            commentService.comment(post.getId(), register.getId(), null, "댓글 " + i);
        }

        // when
        Page<CommentResult> firstPage = commentFacade.listByPost(
                post.getId(),
                PageRequest.of(0, 50, Sort.by(Sort.Direction.DESC, "id"))
        );

        Page<CommentResult> secondPage = commentFacade.listByPost(
                post.getId(),
                PageRequest.of(1, 50, Sort.by(Sort.Direction.DESC, "id"))
        );

        // then
        assertThat(firstPage).isNotNull();
        assertThat(secondPage).isNotNull();

        assertThat(firstPage.getContent()).hasSize(50);
        assertThat(secondPage.getContent()).hasSize(50);

        Long firstPageTop = firstPage.getContent().get(0).commentId();
        Long secondPageTop = secondPage.getContent().get(0).commentId();
        Long firstPageLast = firstPage.getContent().get(49).commentId();

        // 첫 페이지의 첫 댓글이 더 최신이어야 한다
        assertThat(firstPageTop).isGreaterThan(secondPageTop);

        // 첫 페이지의 마지막 댓글보다 두 번째 페이지의 첫 댓글이 더 오래되어야 한다
        assertThat(firstPageLast).isGreaterThan(secondPageTop);
    }
}