package consome.domain.level;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

@Getter
@RequiredArgsConstructor
public enum LevelInfo {

    LEVEL_1(1, 0, "뉴비"),
    LEVEL_2(2, 200, "초보"),
    LEVEL_3(3, 500, "입문"),
    LEVEL_4(4, 1000, "견습"),
    LEVEL_5(5, 1800, "수습"),
    LEVEL_6(6, 3000, "일반"),
    LEVEL_7(7, 5000, "숙련"),
    LEVEL_8(8, 8000, "베테랑"),
    LEVEL_9(9, 12000, "고수"),
    LEVEL_10(10, 18000, "달인"),
    LEVEL_11(11, 26000, "장인"),
    LEVEL_12(12, 38000, "명인"),
    LEVEL_13(13, 55000, "대가"),
    LEVEL_14(14, 80000, "거장"),
    LEVEL_15(15, 115000, "마스터"),
    LEVEL_16(16, 160000, "그랜드마스터"),
    LEVEL_17(17, 220000, "챔피언"),
    LEVEL_18(18, 300000, "레전드"),
    LEVEL_19(19, 400000, "신화"),
    LEVEL_20(20, 500000, "전설"),
    LEVEL_21(21, 500000, "명예"),
    LEVEL_22(22, 500000, "영웅");

    private final int level;
    private final int requiredExp;
    private final String name;

    public static LevelInfo fromLevel(int level) {
        return Arrays.stream(values())
                .filter(info -> info.level == level)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 레벨: " + level));
    }

    public static LevelInfo calculateLevel(int exp) {
        return Arrays.stream(values())
                .filter(info -> info.level <= 20)
                .filter(info -> info.requiredExp <= exp)
                .reduce((first, second) -> second)
                .orElse(LEVEL_1);
    }

    public LevelInfo getNextLevel() {
        if (this.level >= 22) {
            return null;
        }
        return fromLevel(this.level + 1);
    }
}
