package consome.application.level;

public record LevelResult(
        int level,
        int totalExp,
        int requiredExp,
        Integer nextLevelExp,
        String levelName
) {
}
