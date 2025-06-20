package consome.domain.comment;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;

    @Transactional
    public Comment write(Long postId, Long userId, Long parentId, String content) {

        if (parentId == null) {
            Comment comment = Comment.reply(postId, userId, null, content, 0);
            commentRepository.save(comment);
            return comment;
        } else {
            Comment parentComment = commentRepository.findById(parentId)
                    .orElseThrow(() -> new IllegalArgumentException("잘못된 댓글입니다."));

            int maxStep = commentRepository.findMaxStepByParentId(parentId)
                    .orElse(0);

            Comment comment = Comment.reply(postId, userId, parentComment, content, maxStep);
            commentRepository.updateStepsOtherReply(postId, parentComment.getRef(), comment.getStep());
            commentRepository.save(comment);
            return comment;
        }
    }

    @Transactional(readOnly = true)
    public List<Comment> findByPostIdOrderByRefAscStepAsc(Long postId) {
        return commentRepository.findByPostIdOrderByRefAscStepAsc(postId).stream()
                .map(comment -> new Comment(
                        comment.getPostId(),
                        comment.getUserId(),
                        comment.getParentId(),
                        comment.getRef(),
                        comment.getStep(),
                        comment.getDepth(),
                        comment.isDeleted() ? "삭제된 댓글입니다." : comment.getContent()
                ))
                .toList();
    }

    @Transactional
    public void delete(Long commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("잘못된 댓글입니다."));
        comment.delete();
        commentRepository.save(comment);
    }
}