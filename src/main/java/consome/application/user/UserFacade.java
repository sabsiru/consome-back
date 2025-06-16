package consome.application.user;


import consome.domain.point.PointHistoryType;
import consome.domain.point.PointService;
import consome.domain.post.Post;
import consome.domain.post.PostService;
import consome.domain.user.User;
import consome.domain.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserFacade {

    private final UserService userService;
    private final PointService pointService;
    private final PostService postService;

    public Long register(UserCommand command) {
        User user = userService.register(command.getLoginId(), command.getNickname(), command.getPassword());
        pointService.initialize(user.getId());
        return user.getId();
    }

    @Transactional
    public Post write(Post post) {
        pointService.earn(post.getAuthorId(), PointHistoryType.POST_WRITE);
        return postService.write(post);
    }

    @Transactional
    public Post edit(String title, String content, Long postId, Long userId) {
        return postService.edit(title, content, postId, userId);
    }

    @Transactional
    public void delete(Long postId, Long userId) {
        postService.delete(postId, userId);
        pointService.penalize(userId, PointHistoryType.POST_DEL);
    }

    @Transactional
    public void like(Post post, Long userId) {
        postService.like(post, userId);
        pointService.earn(post.getAuthorId(), PointHistoryType.POST_LIKE);
    }

    @Transactional
    public void dislike(Post post, Long userId) {
        postService.dislike(post, userId);
        pointService.penalize(post.getAuthorId(), PointHistoryType.POST_DISLIKE);
    }

    @Transactional
    public void increaseViewCount(Long postId, Long userId, String userIp) {
        postService.increaseViewCount(postId, userId, userIp);
    }
}
