package consome.domain.level;

import consome.domain.level.entity.UserLevel;
import consome.domain.level.repository.UserLevelRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LevelService {

    private static final int INITIAL_EXP = 100;

    private final UserLevelRepository userLevelRepository;

    public UserLevel initialize(Long userId) {
        UserLevel userLevel = UserLevel.initialize(userId, INITIAL_EXP);
        return userLevelRepository.save(userLevel);
    }

    public UserLevel getUserLevel(Long userId) {
        return userLevelRepository.findById(userId)
                .orElseThrow(() -> new IllegalStateException("유저 레벨 정보를 찾을 수 없습니다."));
    }

    @Transactional
    public UserLevel syncAndUpdateLevel(Long userId, int currentPoint) {
        UserLevel userLevel = getUserLevel(userId);
        userLevel.syncExp(currentPoint);

        if (userLevel.getLevel() >= 21) {
            return userLevel;
        }

        int newLevel = LevelInfo.calculateLevel(currentPoint).getLevel();
        if (newLevel != userLevel.getLevel() && newLevel <= 20) {
            userLevel.updateLevel(newLevel);
        }

        return userLevel;
    }

    public List<UserLevel> getLevel20Users() {
        return userLevelRepository.findAllLevel20UsersOrderByExpDesc();
    }

    public long countLevel20Users() {
        return userLevelRepository.countLevel20Users();
    }

    @Transactional
    public void updateRankingLevel(Long userId, int rankingLevel) {
        UserLevel userLevel = getUserLevel(userId);
        userLevel.updateLevel(rankingLevel);
    }
}
