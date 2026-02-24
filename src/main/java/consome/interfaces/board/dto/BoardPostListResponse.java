package consome.interfaces.board.dto;

import consome.application.post.PostPagingResult;

import java.util.List;

public record BoardPostListResponse(
        Long boardId,
        String boardName,
        String description,
        boolean writeEnabled,
        boolean commentEnabled,
        List<BoardPostResponse> posts,
        List<ManagerResponse> managers,
        int page,
        int size,
        long totalElements,
        int totalPages
) {
    public record ManagerResponse(Long userId, String nickname) {}

    public static BoardPostListResponse from (PostPagingResult result){
        List<BoardPostResponse> posts = result.posts().stream()
                .map(BoardPostResponse::from)
                .toList();

        List<ManagerResponse> managers = result.managers().stream()
                .map(m -> new ManagerResponse(m.userId(), m.nickname()))
                .toList();

        return new BoardPostListResponse(
                result.boardId(),
                result.boardName(),
                result.description(),
                result.writeEnabled(),
                result.commentEnabled(),
                posts,
                managers,
                result.page(),
                result.size(),
                result.totalElements(),
                result.totalPages()
        );
    }
}
