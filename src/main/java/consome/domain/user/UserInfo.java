package consome.domain.user;

import com.querydsl.core.annotations.QueryProjection;

public record UserInfo(
        Long userId,
        String loginId,
        String nickname,
        Role role,
        int userPoint,
        int level
) {
    @QueryProjection
    public UserInfo {

    }
}
