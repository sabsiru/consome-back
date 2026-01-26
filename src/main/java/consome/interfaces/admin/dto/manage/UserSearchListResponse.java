package consome.interfaces.admin.dto.manage;

import java.util.List;

public record UserSearchListResponse(
        List<UserSearchResponse> users,
        int page,
        int size,
        long totalElements,
        int totalPages
) {
    public static UserSearchListResponse from(consome.application.user.UserSearchPagingResult result) {
        List<UserSearchResponse> users = result.users().stream()
                .map(UserSearchResponse::from)
                .toList();

        return new UserSearchListResponse(
                users,
                result.page(),
                result.size(),
                result.totalElements(),
                result.totalPages()
        );
    }
}
