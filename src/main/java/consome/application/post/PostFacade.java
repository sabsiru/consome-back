package consome.application.post;

import consome.domain.point.PointHistoryType;
import consome.domain.point.PointService;
import consome.domain.post.entity.Post;
import consome.domain.post.PostService;
import consome.domain.post.entity.PostStat;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PostFacade {

    private final PostService postService;
    private final PointService pointService;

    @Transactional
    public PostResult post(PostCommand command) {
        pointService.earn(command.refUserId(), PointHistoryType.POST_WRITE);
        Post post = postService.post(command.refBoardId(), command.refCategoryId(), command.refUserId(), command.title(), command.content());
        return PostResult.of(post.getId());

    }

    @Transactional
    public EditResult edit(String content, Long postId, Long userId) {
        Post post = postService.edit(content, postId, userId);
        return EditResult.of(post.getId(), post.getUpdatedAt());
    }

    @Transactional
    public Post delete(Long postId, Long userId) {
        pointService.penalize(userId, PointHistoryType.POST_DEL);
        return postService.delete(postId, userId);
    }

    @Transactional
    public PostStat like(Post post, Long userId) {
        pointService.earn(post.getRefUserId(), PointHistoryType.POST_LIKE);
        postService.like(post, userId);

        return postService.getPostStat(post.getId());
    }

    @Transactional
    public PostStat dislike(Post post, Long userId) {
        postService.dislike(post, userId);
        pointService.penalize(post.getRefUserId(), PointHistoryType.POST_DISLIKE);

        return postService.getPostStat(post.getId());
    }

    @Transactional
    public PostStat increaseViewCount(Long postId, String userIp, Long userId) {
        postService.increaseViewCount(postId, userIp, userId);

        return postService.getPostStat(postId);
    }

    public Post getPost(Long postId) {
        return postService.getPost(postId);
    }
}
