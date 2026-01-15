package consome.interfaces.comment.dto;

import java.util.List;

public record CommentPageResponse(
        List<CommentListResponse> comments,
        int currentPage,
        int totalPages,
        long totalElements,
        boolean first,
        boolean last
) {}