package consome.application.comment;

import org.springframework.data.domain.Page;

import java.util.List;

public record CommentPageResult(
        List<CommentListResult> popularComments,
        Page<CommentListResult> comments
) {
}
