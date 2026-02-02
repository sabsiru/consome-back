package consome.domain.point;

import consome.domain.point.repository.PointHistoryRepository;
import consome.domain.point.repository.PointRepository;
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
        pointHistoryRepository.save(PointHistory.create(userId, initializedPoint.getUserPoint(), PointHistoryType.REGISTER, 0, initializedPoint.getUserPoint()));
        return initializedPoint.getUserPoint();
    }

    public int earn(Long userId, PointHistoryType pointHistoryType) {
        Point point = findPointByUserId(userId);
        point.earn(pointHistoryType.getPoint());
        Point earnPoint = pointRepository.save(point);
        pointHistoryRepository.save(PointHistory.create(userId, pointHistoryType.getPoint(), pointHistoryType, point.getUserPoint(), earnPoint.getUserPoint()));
        return earnPoint.getUserPoint();
    }

    public int penalize(Long userId, PointHistoryType pointHistoryType) {
        Point point = findPointByUserId(userId);
        point.penalize(pointHistoryType.getPoint());
        Point penalizedPoint = pointRepository.save(point);
        pointHistoryRepository.save(PointHistory.create(userId, pointHistoryType.getPoint(), pointHistoryType, point.getUserPoint(), penalizedPoint.getUserPoint()));
        return penalizedPoint.getUserPoint();
    }

    public int getCurrentPoint(Long userId) {
        return findPointByUserId(userId).getUserPoint();
    }

    public Point findPointByUserId(Long userId) {
        return pointRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalStateException("사용자를 찾을 수 없습니다."));
    }
}
