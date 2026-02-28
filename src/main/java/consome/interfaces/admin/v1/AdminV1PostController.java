package consome.interfaces.admin.v1;

import consome.application.admin.AdminPostFacade;
import consome.application.admin.PostPinResult;
import consome.infrastructure.security.CustomUserDetails;
import consome.interfaces.admin.dto.PinnedPostReorderRequest;
import consome.interfaces.admin.dto.PostPinRequest;
import consome.interfaces.admin.dto.PostPinResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/admin/posts")
public class AdminV1PostController {

    private final AdminPostFacade adminPostFacade;

    @PatchMapping("/{postId}/pin")
    public PostPinResponse togglePin(@PathVariable Long postId,
                                      @RequestBody PostPinRequest request,
                                      @AuthenticationPrincipal CustomUserDetails userDetails) {
        PostPinResult result = adminPostFacade.togglePin(
                postId,
                request.isPinned(),
                request.pinnedOrder(),
                userDetails.getUserId(),
                userDetails.getRole()
        );
        return PostPinResponse.from(result);
    }

    @PutMapping("/pinned/reorder")
    public ResponseEntity<Void> reorderPinnedPosts(@RequestBody PinnedPostReorderRequest request,
                                                    @AuthenticationPrincipal CustomUserDetails userDetails) {
        List<AdminPostFacade.PinnedOrderCommand> commands = request.orders().stream()
                .map(o -> new AdminPostFacade.PinnedOrderCommand(o.postId(), o.pinnedOrder()))
                .toList();

        adminPostFacade.reorderPinnedPosts(
                request.boardId(),
                commands,
                userDetails.getUserId(),
                userDetails.getRole()
        );
        return ResponseEntity.noContent().build();
    }
}
