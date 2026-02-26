package consome.application.admin;

import consome.domain.admin.Category;
import consome.domain.admin.repository.BoardManagerRepository;
import consome.domain.admin.repository.CategoryRepository;
import consome.domain.common.exception.BusinessException;
import consome.domain.post.PostService;
import consome.domain.post.entity.Post;
import consome.domain.user.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminPostFacade {

    private final PostService postService;
    private final BoardManagerRepository boardManagerRepository;
    private final CategoryRepository categoryRepository;

    @Transactional
    public PostPinResult togglePin(Long postId, Boolean isPinned, Integer pinnedOrder, Long userId, Role userRole) {
        Post post = postService.getPost(postId);

        // 권한 체크: ADMIN이거나 해당 게시판의 MANAGER
        if (userRole != Role.ADMIN) {
            boolean isBoardManager = boardManagerRepository.existsByBoardIdAndUserId(post.getBoardId(), userId);
            if (!isBoardManager) {
                throw new BusinessException("FORBIDDEN", "게시글을 고정할 권한이 없습니다.");
            }
        }

        if (isPinned) {
            // pinnedOrder가 null이면 자동 계산 (최대값 + 1)
            Integer order = pinnedOrder;
            if (order == null) {
                order = postService.getMaxPinnedOrder(post.getBoardId())
                        .map(max -> max + 1)
                        .orElse(1);
            }

            // 공지사항 카테고리 찾기
            Category noticeCategory = categoryRepository.findByBoardIdAndName(post.getBoardId(), "공지사항")
                    .orElseThrow(() -> new BusinessException("NOTICE_CATEGORY_NOT_FOUND", "공지사항 카테고리를 찾을 수 없습니다."));

            post.pin(order, noticeCategory.getId());
        } else {
            post.unpin();
        }

        postService.save(post);

        return new PostPinResult(
                post.getId(),
                post.isPinned(),
                post.getPinnedOrder(),
                isPinned ? "게시글이 상단에 고정되었습니다." : "게시글 고정이 해제되었습니다."
        );
    }

    @Transactional
    public void reorderPinnedPosts(Long boardId, List<PinnedOrderCommand> commands, Long userId, Role userRole) {
        // 권한 체크: ADMIN이거나 해당 게시판의 MANAGER
        if (userRole != Role.ADMIN) {
            boolean isBoardManager = boardManagerRepository.existsByBoardIdAndUserId(boardId, userId);
            if (!isBoardManager) {
                throw new BusinessException("FORBIDDEN", "게시글 순서를 변경할 권한이 없습니다.");
            }
        }

        List<PostService.PinnedPostOrder> orders = commands.stream()
                .map(c -> new PostService.PinnedPostOrder(c.postId(), c.pinnedOrder()))
                .toList();
        postService.reorderPinnedPosts(boardId, orders);
    }

    public record PinnedOrderCommand(Long postId, Integer pinnedOrder) {}
}
