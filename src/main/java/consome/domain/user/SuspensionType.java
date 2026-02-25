package consome.domain.user;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum SuspensionType {
    DAY_1(1, "1일 정지"),
    DAY_2(2, "2일 정지"),
    DAY_3(3, "3일 정지"),
    DAY_7(7, "7일 정지"),
    DAY_15(15, "15일 정지"),
    DAY_30(30, "30일 정지"),
    PERMANENT(0, "영구 정지");

    private final int days;
    private final String description;

    public boolean isPermanent() {
        return this == PERMANENT;
    }
}
