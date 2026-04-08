package consome.interfaces.post.v1;

import consome.application.post.EditResult;
import consome.application.post.ImageUploadResult;
import consome.application.post.PostFacade;
import consome.application.post.PostResult;
import consome.application.post.VideoUploadResult;
import consome.domain.post.entity.Post;
import consome.domain.post.entity.PostStat;
import consome.infrastructure.aop.RequireEmailVerified;
import consome.infrastructure.security.CustomUserDetails;
import consome.interfaces.post.dto.*;
import consome.interfaces.post.mapper.PostRequestMapper;
import consome.interfaces.post.mapper.PostResponseMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/posts")
public class PostV1Controller {

    private final PostFacade postFacade;

    @PostMapping
    @RequireEmailVerified
    public ResponseEntity<PostResponse> post(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody @Valid PostRequest request) {
        PostResult result = postFacade.post(PostRequestMapper.toPostCommand(request, userDetails.getUserId()));
        PostResponse response = PostResponseMapper.toResponse(result);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping(value = "/images", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ImageUploadResponse> uploadImages(
            @RequestPart("images") List<MultipartFile> images) {
        long maxImageSize = 5 * 1024 * 1024; // 5MB
        for (MultipartFile image : images) {
            if (image.getSize() > maxImageSize) {
                return ResponseEntity.badRequest().build();
            }
        }
        List<ImageUploadResult> results = postFacade.uploadImages(images);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ImageUploadResponse.from(results));
    }

    @PostMapping(value = "/videos", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<VideoUploadResponse> uploadVideos(
            @RequestPart("videos") List<MultipartFile> videos) {
        long maxVideoSize = 30 * 1024 * 1024; // 30MB
        for (MultipartFile video : videos) {
            if (video.getSize() > maxVideoSize) {
                return ResponseEntity.badRequest().build();
            }
        }
        List<VideoUploadResult> results = postFacade.uploadVideos(videos);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(VideoUploadResponse.from(results));
    }

    @GetMapping("/{postId}")
    public ResponseEntity<PostDetailResponse> getPostDetail(@PathVariable Long postId,
                                                            HttpServletRequest request,
                                                            @AuthenticationPrincipal CustomUserDetails userDetails) {
        Long userId = userDetails != null ? userDetails.getUserId() : null;
        String userIp = request.getRemoteAddr();
        PostStat stat = postFacade.increaseViewCount(postId, userIp, userId);
        Post post = postFacade.getPost(postId);

        var author = postFacade.getUser(post.getUserId());
        int authorLevel = postFacade.getAuthorLevel(post.getUserId());

        boolean hasLiked = userId != null && postFacade.hasLiked(postId, userId);
        boolean hasDisliked = userId != null && postFacade.hasDisliked(postId, userId);

        boolean commentEnabled = postFacade.isCommentEnabled(post.getBoardId());
        String boardName = postFacade.getBoardName(post.getBoardId());

        return ResponseEntity.ok(PostDetailResponse.of(post, stat, author.getNickname(), author.getRole().name(), authorLevel, boardName, hasLiked, hasDisliked, commentEnabled));
    }

    @PutMapping("/{postId}")
    public ResponseEntity<EditResponse> edit(
            @PathVariable Long postId,
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestPart("request") @Valid EditRequest request,
            @RequestPart(value = "images", required = false) List<MultipartFile> images) {
        EditResult result = postFacade.edit(request.title(), request.categoryId(), request.content(), postId, userDetails.getUserId(), images);
        EditResponse response = EditResponse.from(result);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{postId}")
    public ResponseEntity<Void> delete(@PathVariable Long postId,
                                       @AuthenticationPrincipal CustomUserDetails userDetails) {
        postFacade.delete(postId, userDetails.getUserId());
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{postId}/like")
    public ResponseEntity<PostStatResponse> like(@PathVariable Long postId,
                                                 @AuthenticationPrincipal CustomUserDetails userDetails) {
        Post post = postFacade.getPost(postId);
        PostStat stat = postFacade.like(post, userDetails.getUserId());
        PostStatResponse response = PostStatResponse.from(stat);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/{postId}/dislike")
    public ResponseEntity<PostStatResponse> dislike(@PathVariable Long postId,
                                                    @AuthenticationPrincipal CustomUserDetails userDetails) {
        Post post = postFacade.getPost(postId);
        PostStat stat = postFacade.dislike(post, userDetails.getUserId());
        PostStatResponse response = PostStatResponse.from(stat);

        return ResponseEntity.ok(response);
    }


}
