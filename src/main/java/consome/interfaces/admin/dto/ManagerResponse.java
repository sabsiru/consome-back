package consome.interfaces.admin.dto;

import consome.application.admin.ManagerResult;

public record ManagerResponse(Long userId, String nickname, String boardName) {
    public static ManagerResponse from(ManagerResult result) {
        return new ManagerResponse(result.userId(), result.nickname(), result.boardName());
    }
}
