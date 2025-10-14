package consome.interfaces.comment.dto;

import java.util.List;

public record CommentPageResponse(
        List<CommentResponse> comments,
        int currentPage,
        int totalPages,
        long totalElements,
        boolean first,
        boolean last
) {}