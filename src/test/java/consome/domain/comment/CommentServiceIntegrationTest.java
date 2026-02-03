package consome.domain.comment;

import consome.domain.comment.repository.CommentReactionRepository;
import consome.domain.comment.repository.CommentRepository;
import consome.domain.post.ReactionType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;


@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class CommentServiceIntegrationTest {

    @Autowired
    private CommentService commentService;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private CommentReactionRepository commentReactionRepository;

    @BeforeEach
    void resetDatabase() {
        commentRepository.deleteAll();
    }

    private Long boardId = 1L;
    private Long userId = 100L;
    private Long postId = 1L;

    @Test
    void 대댓글_작성시_step과depth_정상적용() {
        //given
        String content = "테스트 댓글";

        //when
        Comment parentComment = commentService.comment(boardId, userId, null, content);
        //then
        assertThat(parentComment.getStep()).isEqualTo(0);
        assertThat(parentComment.getDepth()).isEqualTo(0);
    }

    @Test
    void 대댓글_작성시_parentComment의_step과depth_정상적용() {
        //given
        String content = "테스트 댓글";

        Comment parentComment = commentService.comment(boardId, userId, null, content);

        System.out.println("parentComment.getRef() = " + parentComment.getRef());

        //when
        Comment replyComment = commentService.comment(boardId, userId, parentComment.getId(), "답글 댓글");

        System.out.println("get Ref: " + replyComment.getRef() +
                ", get Step: " + replyComment.getStep() +
                ", get Depth: " + replyComment.getDepth());
        //then
        assertThat(replyComment.getRef()).isEqualTo(parentComment.getRef());
        assertThat(replyComment.getStep()).isEqualTo(parentComment.getStep() + 1);
        assertThat(replyComment.getDepth()).isEqualTo(parentComment.getDepth() + 1);
    }
// facade로 옮김
//    @Test
//    void 대대댓글_정상작동_확인() {
//        //given
//
//        //when
//        Comment parentComment = commentService.comment(postId, userId, null, "댓글1");
//        Comment parentComment2 = commentService.comment(postId, userId, null, "댓글2");
//        Comment parentComment3 = commentService.comment(postId, userId, null, "댓글3");
//        Comment replyComment = commentService.comment(postId, userId, parentComment.getId(), "댓글1의 대댓글1");
//        Comment replyComment2 = commentService.comment(postId, 101L, parentComment2.getId(), "댓글2의 대댓글2");
//        Comment replyReplyComment = commentService.comment(postId, userId, replyComment2.getId(), "대댓글2의 댓글1");
//        Comment replyComment3 = commentService.comment(postId, 101L, parentComment.getId(), "댓글1의 대댓글2");
//        Comment replyReplyComment2 = commentService.comment(postId, userId, replyComment.getId(), "대댓글1의 대대댓글1");
//        Comment replyReplyComment3 = commentService.comment(postId, userId, replyReplyComment2.getId(), "대대댓글1의 대대대댓글1");
//
//        //then
//        List<Comment> comments = commentService.findByPostIdOrderByRefAscStepAsc(postId);
//
//        /*
//         * 댓글1
//         *  ㄴ 댓글1의 대댓글1
//         *     ㄴ 대댓글1의 대대댓글1
//         *       ㄴ 대대댓글1의 대대대댓글1
//         *  * 댓글1의 대댓글2
//         * 댓글2
//         *  ㄴ 댓글2의 대댓글2
//         *      ㄴ 대댓글2의 댓글1
//         * 댓글3
//         * */
//        assertThat(comments).hasSize(9);
//        assertThat(comments.get(0).getContent()).isEqualTo("댓글1");
//        assertThat(comments.get(1).getContent()).isEqualTo("댓글1의 대댓글1");
//        assertThat(comments.get(2).getContent()).isEqualTo("대댓글1의 대대댓글1");
//        assertThat(comments.get(3).getContent()).isEqualTo("대대댓글1의 대대대댓글1");
//    }

    @Test
    void 댓글_수정_테스트() {
        //given
        String originalContent = "원본 댓글 내용";
        String updatedContent = "수정된 댓글 내용";
        Comment comment = commentService.comment(postId, userId, null, originalContent);

        //when
        Comment edit = commentService.edit(comment.getId(), userId, updatedContent);

        //then
        assertThat(edit.getContent()).isEqualTo(updatedContent);
    }

//    @Test
//    void 댓글_삭제_테스트() {
//        // given
//        Comment comment1 = commentService.comment(postId, userId, null, "댓글1");
//        Comment comment2 = commentService.comment(postId, userId, null, "댓글2");
//
//        // when
//        commentService.delete(comment1.getId(), userId);
//        List<Comment> comments = commentService.findByIdForUpdate(postId, userId, null);
//
//        // then
//        assertThat(comments).hasSize(2);
//        assertThat(comments.get(0).getContent()).isEqualTo("삭제된 댓글입니다.");
//        assertThat(comments.get(1).getContent()).isEqualTo("댓글2");
//    }

    @Test
    void 좋아요_생성_성공() {
        // given
        Comment comment = commentService.comment(postId, userId, null, "테스트 댓글");

        // when
        CommentStat stat = commentService.like(comment.getId(), userId);

        // then
        assertThat(stat.getLikeCount()).isEqualTo(1);
        assertThat(commentReactionRepository.findByIdForUpdate(comment.getId(), userId,ReactionType.LIKE)).isPresent();
    }

    @Test
    void 좋아요_중복시_IllegalStateException_발생() {
        //given
        Comment comment = commentService.comment(postId, userId, null, "테스트 댓글");

        //when
        CommentStat stat = commentService.like(comment.getId(), userId);

        //then
        assertThatThrownBy(() -> commentService.like(comment.getId(), userId))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("이미 좋아요를 눌렀습니다.");

    }

    @Test
    void 싫어요_생성_성공() {
        // given
        Comment comment = commentService.comment(postId, userId, null, "테스트 댓글");

        // when
        CommentStat commentStat = commentService.dislike(comment.getId(), userId);

        // then
        assertThat(commentStat.getDislikeCount()).isEqualTo(1);
    }

    @Test
    void 싫어요_중복시_IllegalStateException_발생() {
        //given
        Comment comment = commentService.comment(postId, userId, null, "테스트 댓글");

        //when
        CommentStat dislikeReaction = commentService.dislike(comment.getId(), userId);

        //then
        assertThatThrownBy(() -> commentService.dislike(comment.getId(), userId))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("이미 싫어요를 눌렀습니다.");

    }
}