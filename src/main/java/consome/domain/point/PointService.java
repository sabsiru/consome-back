package consome.domain.point;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PointService {

    private final PointRepository pointRepository;
    private final PointHistoryRepository pointHistoryRepository;

    public int initialize(Long userId) {
        Point point = Point.initialize(userId);
        Point initializedPoint = pointRepository.save(point);
        pointHistoryRepository.save(PointHistory.create(userId, initializedPoint.getPoint(), PointHistoryType.REGISTER, 0, initializedPoint.getPoint()));
        return initializedPoint.getPoint();
    }

    public int earn(Long userId, PointHistoryType pointHistoryType) {
        Point point = findPointByUserId(userId);
        point.earn(pointHistoryType.getPoint());
        Point earnPoint = pointRepository.save(point);
        pointHistoryRepository.save(PointHistory.create(userId, pointHistoryType.getPoint(), pointHistoryType, point.getPoint(), earnPoint.getPoint()));
        return earnPoint.getPoint();
    }

    public int penalize(Long userId, PointHistoryType pointHistoryType) {
        Point point = findPointByUserId(userId);
        point.penalize(pointHistoryType.getPoint());
        Point penalizedPoint = pointRepository.save(point);
        pointHistoryRepository.save(PointHistory.create(userId, pointHistoryType.getPoint(), pointHistoryType, point.getPoint(), penalizedPoint.getPoint()));
        return penalizedPoint.getPoint();
    }

    public int getCurrentPoint(Long userId) {
        return findPointByUserId(userId).getPoint();
    }

    public Point findPointByUserId(Long userId) {
        return pointRepository.findByRefUserId(userId)
                .orElseThrow(() -> new IllegalStateException("사용자를 찾을 수 없습니다."));
    }
}
