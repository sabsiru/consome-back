package consome.domain.level.scheduler;

import consome.domain.level.LevelService;
import consome.domain.level.entity.UserLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class RankingLevelScheduler {

    private final LevelService levelService;

    @Scheduled(cron = "0 0 0 * * *")
    @Transactional
    public void updateRankingLevels() {
        log.info("랭킹 레벨 갱신 시작");

        long totalLevel20 = levelService.countLevel20Users();
        if (totalLevel20 == 0) {
            log.info("레벨 20 유저 없음 - 랭킹 레벨 갱신 스킵");
            return;
        }

        List<UserLevel> level20Users = levelService.getLevel20Users();

        int top01PercentCount = Math.max(1, (int) Math.ceil(totalLevel20 * 0.001));
        int top1PercentCount = Math.max(1, (int) Math.ceil(totalLevel20 * 0.01));

        int updatedTo22 = 0;
        int updatedTo21 = 0;

        for (int i = 0; i < level20Users.size(); i++) {
            UserLevel userLevel = level20Users.get(i);

            if (i < top01PercentCount) {
                levelService.updateRankingLevel(userLevel.getUserId(), 22);
                updatedTo22++;
            } else if (i < top1PercentCount) {
                levelService.updateRankingLevel(userLevel.getUserId(), 21);
                updatedTo21++;
            } else if (userLevel.getLevel() > 20) {
                levelService.updateRankingLevel(userLevel.getUserId(), 20);
            }
        }

        log.info("랭킹 레벨 갱신 완료 - 레벨22: {}명, 레벨21: {}명", updatedTo22, updatedTo21);
    }
}
