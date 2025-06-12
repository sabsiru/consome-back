package consome.domain.point;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PointService {

    private final UserPointRepository userPointRepository;

    public int initialize(Long userId) {
        Point point = Point.initialize(userId);
        Point initializedPoint = userPointRepository.save(point);
        return initializedPoint.getPoint();
    }

    public int earn(Long userId, int amount) {
        Point point = findPointByUserId(userId);
        point.earn(amount);
        Point earnPoint = userPointRepository.save(point);
        return earnPoint.getPoint();
    }

    public int penalize(Long userId, int amount) {
        Point point = findPointByUserId(userId);
        point.penalize(amount);
        Point penalizedPoint = userPointRepository.save(point);
        return penalizedPoint.getPoint();
    }

    public int getCurrentPoint(Long userId) {
        return findPointByUserId(userId).getPoint();
    }

    public Point findPointByUserId(Long userId) {
        return userPointRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalStateException("사용자를 찾을 수 없습니다."));
    }
}
