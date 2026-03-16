package consome.domain.notification.repository;

import consome.application.notification.NotificationResult;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface NotificationQueryRepository {

    Page<NotificationResult> findByUserId(Long userId, Pageable pageable);
}
