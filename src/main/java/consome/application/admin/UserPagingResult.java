package consome.application.admin;

import java.util.List;

public record UserPagingResult(
        List<UserRowResult> users,
        int page,
        int size,
        long totalElements,
        int totalPages
) {
}
