package consome.domain.comment;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;

    public Comment write(Long postId, Long userId, Long parentId, String content) {
        Comment parentComment = null;
        int step = 0;

        if (parentId != null) {
            parentComment = commentRepository.findById(parentId)
                    .orElseThrow(() -> new IllegalArgumentException("부모 댓글을 찾을 수 없습니다."));
            step = commentRepository.findMaxStepByRef(parentComment.getRef()) + 1;
        }

        Comment comment = new Comment(postId, userId, parentComment != null ? parentComment.getId() : null,
                parentComment != null ? parentComment.getRef() : 0,
                step,
                parentComment != null ? parentComment.getDepth() + 1 : 0,
                content);

        return commentRepository.save(comment);
    }
}