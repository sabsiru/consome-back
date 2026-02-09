package consome.application.level;

import consome.domain.level.LevelInfo;
import consome.domain.level.LevelService;
import consome.domain.level.entity.UserLevel;
import consome.domain.point.PointService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class LevelFacade {

    private final LevelService levelService;
    private final PointService pointService;

    @Transactional(readOnly = true)
    public LevelResult getMyLevel(Long userId) {
        return getLevelResult(userId);
    }

    @Transactional(readOnly = true)
    public LevelResult getUserLevel(Long userId) {
        return getLevelResult(userId);
    }

    @Transactional
    public LevelResult syncLevel(Long userId) {
        int currentPoint = pointService.getCurrentPoint(userId);
        UserLevel userLevel = levelService.syncAndUpdateLevel(userId, currentPoint);
        LevelInfo currentLevelInfo = LevelInfo.fromLevel(userLevel.getLevel());
        LevelInfo nextLevelInfo = currentLevelInfo.getNextLevel();

        return new LevelResult(
                userLevel.getLevel(),
                userLevel.getTotalExp(),
                currentLevelInfo.getRequiredExp(),
                nextLevelInfo != null ? nextLevelInfo.getRequiredExp() : null,
                currentLevelInfo.getName()
        );
    }

    private LevelResult getLevelResult(Long userId) {
        UserLevel userLevel = levelService.getUserLevel(userId);
        LevelInfo currentLevelInfo = LevelInfo.fromLevel(userLevel.getLevel());
        LevelInfo nextLevelInfo = currentLevelInfo.getNextLevel();

        return new LevelResult(
                userLevel.getLevel(),
                userLevel.getTotalExp(),
                currentLevelInfo.getRequiredExp(),
                nextLevelInfo != null ? nextLevelInfo.getRequiredExp() : null,
                currentLevelInfo.getName()
        );
    }
}
