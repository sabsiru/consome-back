package consome.interfaces.admin.dto.manage;

import consome.application.admin.UserPagingResult;

import java.util.List;

public record ManageUserListResponse(
        List<ManageUserResponse> users,
        int page,
        int size,
        long totalElements,
        int totalPages
) {
    public static ManageUserListResponse from(UserPagingResult result) {
        List<ManageUserResponse> users = result.users().stream()
                .map(ManageUserResponse::from)
                .toList();

        return new ManageUserListResponse(
                users,
                result.page(),
                result.size(),
                result.totalElements(),
                result.totalPages()
        );
    }
}