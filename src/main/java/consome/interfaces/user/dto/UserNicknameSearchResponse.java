package consome.interfaces.user.dto;

import consome.application.user.UserSearchResult;

public record UserNicknameSearchResponse(
        Long userId,
        String nickname
) {
    public static UserNicknameSearchResponse from(UserSearchResult result) {
        return new UserNicknameSearchResponse(result.userId(), result.nickname());
    }
}
