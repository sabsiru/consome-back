package consome.domain.notification;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(indexes = {
        @Index(name = "idx_notification_user_unread", columnList = "userId, isRead, createdAt DESC"),
        @Index(name = "idx_notification_user_created", columnList = "userId, createdAt DESC")
})
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long userId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private NotificationType type;

    @Column(nullable = false)
    private Long actorId;

    @Column(nullable = false)
    private Long targetId;

    private Long relatedId;

    private Long referenceId;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String message;

    @Column(nullable = false)
    private boolean isRead;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime readAt;

    private Notification(Long userId, NotificationType type, Long actorId,
                         Long targetId, Long relatedId, Long referenceId, String message) {
        this.userId = userId;
        this.type = type;
        this.actorId = actorId;
        this.targetId = targetId;
        this.relatedId = relatedId;
        this.referenceId = referenceId;
        this.message = message;
        this.isRead = false;
        this.createdAt = LocalDateTime.now();
    }

    public static Notification create(Long userId, NotificationType type, Long actorId,
                                       Long targetId, Long relatedId, Long referenceId, String message) {
        return new Notification(userId, type, actorId, targetId, relatedId, referenceId, message);
    }

    public void markAsRead() {
        if (!this.isRead) {
            this.isRead = true;
            this.readAt = LocalDateTime.now();
        }
    }
}
