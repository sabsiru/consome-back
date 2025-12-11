package consome.application.post;

import java.util.List;

public record PostPagingResult(
        List<PostRowResult> posts,
        int page,
        int size,
        long totalElements,
        int totalPages
) {
}
