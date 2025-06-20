package consome.domain.comment;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;


@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class CommentServiceIntegrationTest {

    @Autowired
    private CommentService commentService;

    @Autowired
    private CommentRepository commentRepository;

    @BeforeEach
    void resetDatabase() {
        commentRepository.deleteAll();
    }

    @Test
    void 대댓글_작성시_step과depth_정상적용() {
        //given
        Long boardId = 1L;
        Long userId = 100L;
        String content = "테스트 댓글";

        //when
        Comment parentComment = commentService.write(boardId, userId, null, content);
        //then
        assertThat(parentComment.getStep()).isEqualTo(0);
        assertThat(parentComment.getDepth()).isEqualTo(0);
    }

    @Test
    void 대댓글_작성시_parentComment의_step과depth_정상적용() {
        //given
        Long boardId = 1L;
        Long userId = 100L;
        String content = "테스트 댓글";

        Comment parentComment = commentService.write(boardId, userId, null, content);

        System.out.println("parentComment.getRef() = " + parentComment.getRef());

        //when
        Comment replyComment = commentService.write(boardId, userId, parentComment.getId(), "답글 댓글");

        System.out.println("get Ref: " + replyComment.getRef() +
                ", get Step: " + replyComment.getStep() +
                ", get Depth: " + replyComment.getDepth());
        //then
        assertThat(replyComment.getRef()).isEqualTo(parentComment.getRef());
        assertThat(replyComment.getStep()).isEqualTo(parentComment.getStep() + 1);
        assertThat(replyComment.getDepth()).isEqualTo(parentComment.getDepth() + 1);
    }

    @Test
    void 대대댓글_정상작동_확인() {
        //given
        Long postId = 1L;
        Long userId = 100L;

        //when
        Comment parentComment = commentService.write(postId, userId, null, "댓글1");
        Comment parentComment2 = commentService.write(postId, userId, null, "댓글2");
        Comment parentComment3 = commentService.write(postId, userId, null, "댓글3");
        Comment replyComment = commentService.write(postId, userId, parentComment.getId(), "댓글1의 대댓글1");
        Comment replyComment2 = commentService.write(postId, 101L, parentComment2.getId(), "댓글2의 대댓글2");
        Comment replyReplyComment = commentService.write(postId, userId, replyComment2.getId(), "대댓글2의 댓글1");
        Comment replyComment3 = commentService.write(postId, 101L, parentComment.getId(), "댓글1의 대댓글2");
        Comment replyReplyComment2 = commentService.write(postId, userId, replyComment.getId(), "대댓글1의 대대댓글1");
        Comment replyReplyComment3 = commentService.write(postId, userId, replyReplyComment2.getId(), "대대댓글1의 대대대댓글1");

        //then
        List<Comment> comments = commentService.findByPostIdOrderByRefAscStepAsc(postId);
        for (Comment comment : comments) {
            System.out.println("Comment ID: " + comment.getId() +
                    ", Content: " + comment.getContent() +
                    ", Ref: " + comment.getRef() +
                    ", Step: " + comment.getStep() +
                    ", Depth: " + comment.getDepth());
        }
        /*
         * 댓글1
         *  ㄴ 댓글1의 대댓글1
         *     ㄴ 대댓글1의 대대댓글1
         *       ㄴ 대대댓글1의 대대대댓글1
         *  * 댓글1의 대댓글2
         * 댓글2
         *  ㄴ 댓글2의 대댓글2
         *      ㄴ 대댓글2의 댓글1
         * 댓글3
         * */
        assertThat(comments).hasSize(9);
        assertThat(comments.get(0).getContent()).isEqualTo("댓글1");
        assertThat(comments.get(1).getContent()).isEqualTo("댓글1의 대댓글1");
        assertThat(comments.get(2).getContent()).isEqualTo("대댓글1의 대대댓글1");
        assertThat(comments.get(3).getContent()).isEqualTo("대대댓글1의 대대대댓글1");
    }

    @Test
    void 댓글_삭제_테스트() {
        // given
        Long postId = 1L;
        Long userId = 100L;

        Comment comment1 = commentService.write(postId, userId, null, "댓글1");
        Comment comment2 = commentService.write(postId, userId, null, "댓글2");

        // when
        commentService.delete(comment1.getId());
        List<Comment> comments = commentService.findByPostIdOrderByRefAscStepAsc(postId);

        // then
        assertThat(comments).hasSize(2);
        assertThat(comments.get(0).getContent()).isEqualTo("삭제된 댓글입니다.");
        assertThat(comments.get(1).getContent()).isEqualTo("댓글2");
    }

}