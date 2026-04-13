package consome.interfaces.notification.v1;

import consome.application.notification.NotificationFacade;
import consome.infrastructure.security.CustomUserDetails;
import consome.interfaces.notification.dto.NotificationResponse;
import consome.interfaces.notification.dto.UnreadCountResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/notifications")
public class NotificationV1Controller {

    private final NotificationFacade notificationFacade;

    @GetMapping
    public ResponseEntity<Page<NotificationResponse>> getNotifications(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PageableDefault(size = 20) Pageable pageable) {
        var result = notificationFacade.getNotifications(userDetails.getUserId(), pageable);
        return ResponseEntity.ok(result.map(NotificationResponse::from));
    }

    @GetMapping("/unread-count")
    public ResponseEntity<UnreadCountResponse> getUnreadCount(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        long count = notificationFacade.getUnreadCount(userDetails.getUserId());
        return ResponseEntity.ok(new UnreadCountResponse(count));
    }

    @PatchMapping("/{notificationId}/read")
    public ResponseEntity<NotificationResponse> markAsRead(
            @PathVariable Long notificationId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        var result = notificationFacade.markAsRead(notificationId, userDetails.getUserId());
        return ResponseEntity.ok(NotificationResponse.from(result));
    }

    @PatchMapping("/read-all")
    public ResponseEntity<Void> markAllAsRead(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        notificationFacade.markAllAsRead(userDetails.getUserId());
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{notificationId}")
    public ResponseEntity<Void> delete(
            @PathVariable Long notificationId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        notificationFacade.delete(notificationId, userDetails.getUserId());
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteAll(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        notificationFacade.deleteAll(userDetails.getUserId());
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/sse-token")
    public ResponseEntity<Map<String, String>> createSseToken(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        String token = notificationFacade.createSseToken(userDetails.getUserId());
        return ResponseEntity.ok(java.util.Map.of("token", token));
    }

    @GetMapping(value = "/subscribe", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter subscribe(@RequestParam String token) {
        return notificationFacade.subscribe(token);
    }
}
