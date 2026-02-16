package consome.domain.message.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(indexes = {
        @Index(name = "idx_message_receiver_read", columnList = "receiverId, isRead"),
        @Index(name = "idx_message_sender", columnList = "senderId"),
        @Index(name = "idx_message_created", columnList = "createdAt DESC")
})
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long senderId;

    @Column(nullable = false)
    private Long receiverId;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(nullable = false)
    private int point = 0;

    @Column(nullable = false)
    private boolean isRead = false;

    @Column(nullable = false)
    private boolean isDeletedBySender = false;

    @Column(nullable = false)
    private boolean isDeletedByReceiver = false;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    private LocalDateTime readAt;

    private Message(Long senderId, Long receiverId, String content, int point) {
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.content = content;
        this.point = point;
        this.createdAt = LocalDateTime.now();
    }

    public static Message send(Long senderId, Long receiverId, String content, int point) {
        validateContent(content);
        validatePoint(point);
        return new Message(senderId, receiverId, content, point);
    }

    private static void validateContent(String content) {
        if (content == null || content.isBlank()) {
            throw new IllegalArgumentException("쪽지 내용은 필수입니다.");
        }
        if (content.length() > 2000) {
            throw new IllegalArgumentException("쪽지 내용은 2000자를 초과할 수 없습니다.");
        }
    }

    private static void validatePoint(int point) {
        if (point < 0) {
            throw new IllegalArgumentException("포인트는 0 이상이어야 합니다.");
        }
    }

    public void markAsRead() {
        if (!this.isRead) {
            this.isRead = true;
            this.readAt = LocalDateTime.now();
        }
    }

    public void deleteBySender() {
        this.isDeletedBySender = true;
    }

    public void deleteByReceiver() {
        this.isDeletedByReceiver = true;
    }

    public boolean isSender(Long userId) {
        return this.senderId.equals(userId);
    }

    public boolean isReceiver(Long userId) {
        return this.receiverId.equals(userId);
    }

    public boolean canAccess(Long userId) {
        if (isSender(userId) && !isDeletedBySender) return true;
        if (isReceiver(userId) && !isDeletedByReceiver) return true;
        return false;
    }

    public boolean hasPoint() {
        return this.point > 0;
    }
}
