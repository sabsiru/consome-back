package consome.interfaces.post.v1;

import consome.application.post.EditResult;
import consome.application.post.PostFacade;
import consome.application.post.PostResult;
import consome.domain.post.entity.Post;
import consome.domain.post.entity.PostStat;
import consome.interfaces.post.dto.*;
import consome.interfaces.post.mapper.PostRequestMapper;
import consome.interfaces.post.mapper.PostResponseMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/posts")
public class PostV1Controller {

    private final PostFacade postFacade;

    @PostMapping
    public ResponseEntity<PostResponse> post(@RequestBody @Valid PostRequest request) {
        PostResult result = postFacade.post(PostRequestMapper.toPostCommand(request));
        PostResponse response = PostResponseMapper.toResponse(result);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{postId}")
    public ResponseEntity<EditResponse> edit(@PathVariable Long postId,
                                             @RequestBody @Valid EditRequest request,
                                             @RequestParam Long userId) {
        EditResult result = postFacade.edit(request.content(), postId, userId);
        EditResponse response = EditResponse.from(result);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{postId}")
    public ResponseEntity<Void> delete(@PathVariable Long postId,
                                       @RequestParam Long userId) {
        postFacade.delete(postId, userId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{postId}/like")
    public ResponseEntity<PostStatResponse> like(@PathVariable Long postId,
                                                 @RequestParam Long userId) {
        Post post = postFacade.getPost(postId);
        PostStat stat = postFacade.like(post, userId);
        PostStatResponse response = PostStatResponse.from(stat);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/{postId}/dislike")
    public ResponseEntity<PostStatResponse> dislike(@PathVariable Long postId,
                                                    @RequestParam Long userId) {
        Post post = postFacade.getPost(postId);
        PostStat stat = postFacade.dislike(post, userId);
        PostStatResponse response = PostStatResponse.from(stat);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{postId}/view")
    public ResponseEntity<PostDetailResponse> get(@PathVariable Long postId,
                                                  HttpServletRequest request,
                                                  @RequestParam(required = false) Long userId) {
        String userIp = request.getRemoteAddr();
        PostStat stat = postFacade.increaseViewCount(postId, userIp, userId);
        Post post = postFacade.getPost(postId);

        return ResponseEntity.ok(PostDetailResponse.of(post, stat));
    }


}
