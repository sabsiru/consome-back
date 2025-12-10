package consome.application.user;

import consome.domain.user.UserInfo;

import java.util.List;

public record UserPageResult(
        List<UserInfo> users,
        long totalUsers
) {
}
