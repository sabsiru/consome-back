package consome.domain.common.exception;

import consome.domain.comment.exception.CommentException;
import consome.domain.post.exception.PostException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("예외 클래스 테스트")
class ExceptionTest {

    @Nested
    @DisplayName("BusinessException")
    class BusinessExceptionTest {

        @Test
        @DisplayName("기본 생성자로 code와 message가 설정된다")
        void 기본_생성자_테스트() {
            // when
            BusinessException ex = new BusinessException("TEST_CODE", "테스트 메시지");

            // then
            assertThat(ex.getCode()).isEqualTo("TEST_CODE");
            assertThat(ex.getMessage()).isEqualTo("테스트 메시지");
        }

        @Test
        @DisplayName("BoardNotFound는 boardId를 포함한 메시지를 생성한다")
        void BoardNotFound_테스트() {
            // when
            BusinessException.BoardNotFound ex = new BusinessException.BoardNotFound(123L);

            // then
            assertThat(ex.getCode()).isEqualTo("BOARD_NOT_FOUND");
            assertThat(ex.getMessage()).contains("123");
        }

        @Test
        @DisplayName("CategoryNotFound는 categoryId를 포함한 메시지를 생성한다")
        void CategoryNotFound_테스트() {
            // when
            BusinessException.CategoryNotFound ex = new BusinessException.CategoryNotFound(456L);

            // then
            assertThat(ex.getCode()).isEqualTo("CATEGORY_NOT_FOUND");
            assertThat(ex.getMessage()).contains("456");
        }

        @Test
        @DisplayName("InvalidPassword는 전달된 메시지를 그대로 사용한다")
        void InvalidPassword_테스트() {
            // when
            BusinessException.InvalidPassword ex = new BusinessException.InvalidPassword("비밀번호가 너무 짧습니다");

            // then
            assertThat(ex.getCode()).isEqualTo("INVALID_PASSWORD");
            assertThat(ex.getMessage()).isEqualTo("비밀번호가 너무 짧습니다");
        }

        @Test
        @DisplayName("InvalidPointAmount는 전달된 메시지를 그대로 사용한다")
        void InvalidPointAmount_테스트() {
            // when
            BusinessException.InvalidPointAmount ex = new BusinessException.InvalidPointAmount("포인트는 0 이상이어야 합니다");

            // then
            assertThat(ex.getCode()).isEqualTo("INVALID_POINT_AMOUNT");
            assertThat(ex.getMessage()).isEqualTo("포인트는 0 이상이어야 합니다");
        }
    }

    @Nested
    @DisplayName("PostException")
    class PostExceptionTest {

        @Test
        @DisplayName("NotFound는 postId를 포함한 메시지를 생성한다")
        void NotFound_with_id_테스트() {
            // when
            PostException.NotFound ex = new PostException.NotFound(100L);

            // then
            assertThat(ex.getCode()).isEqualTo("POST_NOT_FOUND");
            assertThat(ex.getMessage()).contains("100");
        }

        @Test
        @DisplayName("NotFound 기본 생성자는 일반 메시지를 생성한다")
        void NotFound_default_테스트() {
            // when
            PostException.NotFound ex = new PostException.NotFound();

            // then
            assertThat(ex.getCode()).isEqualTo("POST_NOT_FOUND");
            assertThat(ex.getMessage()).isEqualTo("게시글을 찾을 수 없습니다.");
        }

        @Test
        @DisplayName("Unauthorized는 action을 포함한 메시지를 생성한다")
        void Unauthorized_테스트() {
            // when
            PostException.Unauthorized ex = new PostException.Unauthorized("수정");

            // then
            assertThat(ex.getCode()).isEqualTo("POST_UNAUTHORIZED");
            assertThat(ex.getMessage()).contains("수정");
        }

        @Test
        @DisplayName("AlreadyLiked 예외 생성")
        void AlreadyLiked_테스트() {
            // when
            PostException.AlreadyLiked ex = new PostException.AlreadyLiked();

            // then
            assertThat(ex.getCode()).isEqualTo("POST_ALREADY_LIKED");
        }

        @Test
        @DisplayName("AlreadyDisliked 예외 생성")
        void AlreadyDisliked_테스트() {
            // when
            PostException.AlreadyDisliked ex = new PostException.AlreadyDisliked();

            // then
            assertThat(ex.getCode()).isEqualTo("POST_ALREADY_DISLIKED");
        }

        @Test
        @DisplayName("NotLiked 예외 생성")
        void NotLiked_테스트() {
            // when
            PostException.NotLiked ex = new PostException.NotLiked();

            // then
            assertThat(ex.getCode()).isEqualTo("POST_NOT_LIKED");
        }

        @Test
        @DisplayName("NotDisliked 예외 생성")
        void NotDisliked_테스트() {
            // when
            PostException.NotDisliked ex = new PostException.NotDisliked();

            // then
            assertThat(ex.getCode()).isEqualTo("POST_NOT_DISLIKED");
        }
    }

    @Nested
    @DisplayName("CommentException")
    class CommentExceptionTest {

        @Test
        @DisplayName("NotFound는 commentId를 포함한 메시지를 생성한다")
        void NotFound_with_id_테스트() {
            // when
            CommentException.NotFound ex = new CommentException.NotFound(200L);

            // then
            assertThat(ex.getCode()).isEqualTo("COMMENT_NOT_FOUND");
            assertThat(ex.getMessage()).contains("200");
        }

        @Test
        @DisplayName("NotFound 기본 생성자는 일반 메시지를 생성한다")
        void NotFound_default_테스트() {
            // when
            CommentException.NotFound ex = new CommentException.NotFound();

            // then
            assertThat(ex.getCode()).isEqualTo("COMMENT_NOT_FOUND");
            assertThat(ex.getMessage()).isEqualTo("잘못된 댓글입니다.");
        }

        @Test
        @DisplayName("StatsNotFound는 commentId를 포함한 메시지를 생성한다")
        void StatsNotFound_테스트() {
            // when
            CommentException.StatsNotFound ex = new CommentException.StatsNotFound(300L);

            // then
            assertThat(ex.getCode()).isEqualTo("COMMENT_STATS_NOT_FOUND");
            assertThat(ex.getMessage()).contains("300");
        }

        @Test
        @DisplayName("Unauthorized는 action을 포함한 메시지를 생성한다")
        void Unauthorized_테스트() {
            // when
            CommentException.Unauthorized ex = new CommentException.Unauthorized("삭제");

            // then
            assertThat(ex.getCode()).isEqualTo("COMMENT_UNAUTHORIZED");
            assertThat(ex.getMessage()).contains("삭제");
        }

        @Test
        @DisplayName("AlreadyDeleted 예외 생성")
        void AlreadyDeleted_테스트() {
            // when
            CommentException.AlreadyDeleted ex = new CommentException.AlreadyDeleted();

            // then
            assertThat(ex.getCode()).isEqualTo("COMMENT_ALREADY_DELETED");
        }

        @Test
        @DisplayName("AlreadyLiked 예외 생성")
        void AlreadyLiked_테스트() {
            // when
            CommentException.AlreadyLiked ex = new CommentException.AlreadyLiked();

            // then
            assertThat(ex.getCode()).isEqualTo("COMMENT_ALREADY_LIKED");
        }

        @Test
        @DisplayName("AlreadyDisliked 예외 생성")
        void AlreadyDisliked_테스트() {
            // when
            CommentException.AlreadyDisliked ex = new CommentException.AlreadyDisliked();

            // then
            assertThat(ex.getCode()).isEqualTo("COMMENT_ALREADY_DISLIKED");
        }
    }
}
