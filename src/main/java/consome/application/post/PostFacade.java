package consome.application.post;

import consome.domain.point.PointHistoryType;
import consome.domain.point.PointService;
import consome.domain.post.PopularPostService;
import consome.domain.post.ReactionType;
import consome.domain.post.entity.Post;
import consome.domain.post.PostService;
import consome.domain.post.entity.PostImage;
import consome.domain.post.entity.PostStat;
import consome.domain.post.entity.TempPostImage;
import consome.domain.post.repository.PostImageRepository;
import consome.domain.post.repository.PostReactionRepository;
import consome.domain.post.repository.TempPostImageRepository;
import consome.infrastructure.storage.FileStorage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PostFacade {

    private final PostService postService;
    private final PointService pointService;
    private final PopularPostService popularPostService;
    private final FileStorage fileStorage;
    private final PostReactionRepository postReactionRepository;
    private final PostImageRepository postImageRepository;
    private final TempPostImageRepository tempPostImageRepository;

    @Transactional
    public PostResult post(PostCommand command) {
        String content = command.content();
        Post post = postService.post(command.boardId(), command.categoryId(),
                command.userId(), command.title(), content);

        List<String> urls = fileStorage.extractImageUrls(content);
        for (String url : urls) {
            tempPostImageRepository.findByUrl(url).ifPresent(temp -> {
                postImageRepository.save(temp.toPostImage(post.getId()));
                tempPostImageRepository.delete(temp);
            });
        }

        return PostResult.of(post.getId());
    }

    @Transactional
    public PostResult postV1(PostCommand command, List<MultipartFile> images) {

        String content = command.content();

        // 이미지 저장 + URL 치환
        if (images != null && !images.isEmpty()) {
            List<String> urls = fileStorage.storeAll(images, "posts");
            content = replaceImageIndexes(content, urls);
        }

        Post post = postService.post(command.boardId(), command.categoryId(), command.userId(), command.title(), content);

        // PostImage 저장
        if (images != null && !images.isEmpty()) {
            List<String> urls = fileStorage.extractImageUrls(content);
            for (int i = 0; i < urls.size(); i++) {
                PostImage postImage = PostImage.create(
                        post.getId(), urls.get(i), extractStoredName(urls.get(i)),
                        images.get(i).getOriginalFilename(),
                        images.get(i).getSize()
                );
                postImageRepository.save(postImage);
            }
        }
        return PostResult.of(post.getId());

    }

    @Transactional
    public EditResult edit(String title, Long categoryId, String content, Long postId, Long userId, List<MultipartFile> images) {
        Post post = postService.getPostForUpdate(postId);

        if (!post.getUserId().equals(userId)) {
            throw new IllegalStateException("작성자만 게시글을 수정할 수 있습니다.");
        }

        // 새 이미지 저장 + URL 치환
        if (images != null && !images.isEmpty()) {
            List<String> urls = fileStorage.storeAll(images, "posts");
            content = replaceImageIndexes(content, urls);
        }

        // 기존 이미지 URL 조회
        List<PostImage> existingImages = postImageRepository.findByPostId(postId);
        List<String> existingUrls = existingImages.stream()
                .map(PostImage::getUrl)
                .toList();

        // 새 content에서 이미지 URL 추출
        List<String> newUrls = fileStorage.extractImageUrls(content);

        // 삭제 대상 찾기
        List<String> deleteTargets = fileStorage.findDeleteTargets(existingUrls, newUrls);

        // 파일 + DB 삭제
        for (String url : deleteTargets) {
            fileStorage.delete(url);
        }
        postImageRepository.deleteByPostId(postId);

        // 새 PostImage 저장
        for (int i = 0; i < newUrls.size(); i++) {
            String url = newUrls.get(i);
            PostImage postImage = PostImage.create(
                    postId, url, extractStoredName(url),
                    extractStoredName(url), 0L
            );
            postImageRepository.save(postImage);
        }

        post.edit(title, categoryId, content);
        return EditResult.of(post.getId(), post.getUpdatedAt());
    }

    @Transactional
    public Post delete(Long postId, Long userId) {
        postImageRepository.deleteByPostId(postId);
        return postService.delete(postId, userId);
    }

    @Transactional
    public PostStat like(Post post, Long userId) {
        pointService.earn(post.getUserId(), PointHistoryType.POST_LIKE);
        postService.like(post, userId);
        popularPostService.updateScore(post.getId());

        return postService.getPostStat(post.getId());
    }

    @Transactional
    public PostStat dislike(Post post, Long userId) {
        postService.dislike(post, userId);
        pointService.penalize(post.getUserId(), PointHistoryType.POST_DISLIKE);

        return postService.getPostStat(post.getId());
    }
    public boolean hasLiked(Long postId, Long userId) {
        return
                postReactionRepository.findByPostIdAndUserIdAndTypeAndDeletedFalse(postId, userId,
                        ReactionType.LIKE).isPresent();
    }

    public boolean hasDisliked(Long postId, Long userId) {
        return
                postReactionRepository.findByPostIdAndUserIdAndTypeAndDeletedFalse(postId, userId,
                        ReactionType.DISLIKE).isPresent();
    }


    @Transactional
    public PostStat increaseViewCount(Long postId, String userIp, Long userId) {
        postService.increaseViewCount(postId, userIp, userId);
        popularPostService.updateScore(postId);

        return postService.getPostStat(postId);
    }

    public Post getPost(Long postId) {
        return postService.getPost(postId);
    }

    private String replaceImageIndexes(String content, List<String> urls) {
        for (int i = 0; i < urls.size(); i++) {
            content = content.replace(
                    "data-image-index=\"" + i + "\"",
                    "src=\"" + urls.get(i) + "\""
            );
        }
        return content;
    }

    private String extractStoredName(String url) {
        return url.substring(url.lastIndexOf("/") + 1);
    }


    @Transactional
    public List<ImageUploadResult> uploadImages(List<MultipartFile> images) {
        return images.stream()
                .map(image -> {
                    String url = fileStorage.store(image, "posts");
                    TempPostImage temp = TempPostImage.create(
                            url,
                            extractStoredName(url),
                            image.getOriginalFilename(),
                            image.getSize()
                    );
                    tempPostImageRepository.save(temp);
                    return new ImageUploadResult(url, image.getOriginalFilename());
                })
                .toList();
    }

    @Transactional
    public List<VideoUploadResult> uploadVideos(List<MultipartFile> videos) {
        return videos.stream()
                .map(video -> {
                    String url = fileStorage.storeAndConvertVideo(video, "videos");
                    return new VideoUploadResult(url, video.getOriginalFilename());
                })
                .toList();
    }
}
