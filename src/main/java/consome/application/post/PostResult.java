package consome.application.post;

public record PostResult(
        Long postId
) {
    public static PostResult of (Long postId){
        return new PostResult(postId);
    }
}
