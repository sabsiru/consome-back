package consome.interfaces.notification.v1;

import consome.application.notification.NotificationFacade;
import consome.interfaces.notification.dto.NotificationResponse;
import consome.interfaces.notification.dto.UnreadCountResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/notifications")
public class NotificationV1Controller {

    private final NotificationFacade notificationFacade;

    @GetMapping
    public ResponseEntity<Page<NotificationResponse>> getNotifications(
            @RequestParam Long userId,
            @PageableDefault(size = 20) Pageable pageable) {
        var result = notificationFacade.getNotifications(userId, pageable);
        return ResponseEntity.ok(result.map(NotificationResponse::from));
    }

    @GetMapping("/unread-count")
    public ResponseEntity<UnreadCountResponse> getUnreadCount(@RequestParam Long userId) {
        long count = notificationFacade.getUnreadCount(userId);
        return ResponseEntity.ok(new UnreadCountResponse(count));
    }

    @PatchMapping("/{notificationId}/read")
    public ResponseEntity<NotificationResponse> markAsRead(
            @PathVariable Long notificationId,
            @RequestParam Long userId) {
        var result = notificationFacade.markAsRead(notificationId, userId);
        return ResponseEntity.ok(NotificationResponse.from(result));
    }

    @PatchMapping("/read-all")
    public ResponseEntity<Void> markAllAsRead(@RequestParam Long userId) {
        notificationFacade.markAllAsRead(userId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping(value = "/subscribe", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter subscribe(@RequestParam String token) {
        return notificationFacade.subscribe(token);
    }
}
