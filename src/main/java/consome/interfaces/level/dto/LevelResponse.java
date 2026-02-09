package consome.interfaces.level.dto;

import consome.application.level.LevelResult;

public record LevelResponse(
        int level,
        int totalExp,
        int requiredExp,
        Integer nextLevelExp,
        String levelName
) {
    public static LevelResponse from(LevelResult result) {
        return new LevelResponse(
                result.level(),
                result.totalExp(),
                result.requiredExp(),
                result.nextLevelExp(),
                result.levelName()
        );
    }
}
