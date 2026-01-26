package consome.application.user;

import java.util.List;

public record UserSearchPagingResult(
        List<UserSearchResult> users,
        int page,
        int size,
        long totalElements,
        int totalPages
) {
}
