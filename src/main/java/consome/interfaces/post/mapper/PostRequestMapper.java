package consome.interfaces.post.mapper;

import consome.application.post.PostCommand;
import consome.interfaces.post.dto.PostRequest;

public class PostRequestMapper {
    public static PostCommand toPostCommand(PostRequest request) {
        return new PostCommand(
                request.boardId(),
                request.categoryId(),
                request.userId(),
                request.title(),
                request.content()
        );
    }
}
