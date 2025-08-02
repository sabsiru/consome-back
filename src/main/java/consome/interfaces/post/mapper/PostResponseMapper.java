package consome.interfaces.post.mapper;

import consome.application.post.PostResult;
import consome.interfaces.post.dto.PostResponse;

public class PostResponseMapper {
    public static PostResponse toResponse(PostResult result) {
        return PostResponse.of(result.postId());
    }
}
