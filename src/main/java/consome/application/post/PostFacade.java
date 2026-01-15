package consome.application.post;

import consome.domain.point.PointHistoryType;
import consome.domain.point.PointService;
import consome.domain.post.entity.Post;
import consome.domain.post.PostService;
import consome.domain.post.entity.PostStat;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class PostFacade {

    private final PostService postService;
    private final PointService pointService;

    @Transactional
    public PostResult post(PostCommand command) {
        //pointService.earn(command.userId(), PointHistoryType.POST_WRITE);

        log.info("BoardId: {}, CategoryId: {}, UserId: {}, Title: {}, Content: {}",
                command.boardId(), command.categoryId(), command.userId(), command.title(), command.content());

        Post post = postService.post(command.boardId(), command.categoryId(), command.userId(), command.title(), command.content());
        return PostResult.of(post.getId());

    }

    @Transactional
    public EditResult edit(String content, Long postId, Long userId) {
        Post post = postService.edit(content, postId, userId);
        return EditResult.of(post.getId(), post.getUpdatedAt());
    }

    @Transactional
    public Post delete(Long postId, Long userId) {
        //pointService.penalize(userId, PointHistoryType.POST_DEL);
        return postService.delete(postId, userId);
    }

    @Transactional
    public PostStat like(Post post, Long userId) {
        pointService.earn(post.getUserId(), PointHistoryType.POST_LIKE);
        postService.like(post, userId);

        return postService.getPostStat(post.getId());
    }

    @Transactional
    public PostStat dislike(Post post, Long userId) {
        postService.dislike(post, userId);
        pointService.penalize(post.getUserId(), PointHistoryType.POST_DISLIKE);

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
