package consome.interfaces.post.dto;

public record PostResponse(
        Long postId,
        String message) {
    public static PostResponse of(Long postId) {
        return new PostResponse(postId, "게시글이 성공적으로 작성되었습니다.");
    }
}

