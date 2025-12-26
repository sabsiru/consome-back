package consome.interfaces.board.dto;

import consome.application.post.PostPagingResult;

import java.util.List;

public record BoardPostListResponse(
        Long boardId,
        String boardName,
        List<BoardPostResponse> posts,
        int page,
        int size,
        long totalElements,
        int totalPages
) {

    public static BoardPostListResponse from (PostPagingResult result){
        List<BoardPostResponse> posts = result.posts().stream()
                .map(BoardPostResponse::from)
                .toList();

        return new BoardPostListResponse(
                result.boardId(),
                result.boardName(),
                posts,
                result.page(),
                result.size(),
                result.totalElements(),
                result.totalPages()
        );
    }
}
