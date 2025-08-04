package consome.application.post;


import consome.domain.point.PointHistoryType;
import consome.domain.point.PointService;
import consome.domain.post.entity.Post;
import consome.domain.post.PostService;
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
        Post post = postService.post(command.boardId(), command.categoryId(), command.refUserId(), command.title(), command.content());
        return PostResult.of(post.getId());

    }

}
