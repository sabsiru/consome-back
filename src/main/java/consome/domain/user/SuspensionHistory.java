package consome.domain.user;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SuspensionHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long userId;

    private Long reportId;

    @Column(nullable = false)
    private Long adminId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SuspensionType suspensionType;

    @Column(nullable = false)
    private String reason;

    @Column(nullable = false)
    private LocalDateTime startAt;

    private LocalDateTime endAt;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    private SuspensionHistory(Long userId, Long reportId, Long adminId,
                              SuspensionType suspensionType, String reason,
                              LocalDateTime startAt, LocalDateTime endAt) {
        this.userId = userId;
        this.reportId = reportId;
        this.adminId = adminId;
        this.suspensionType = suspensionType;
        this.reason = reason;
        this.startAt = startAt;
        this.endAt = endAt;
        this.createdAt = LocalDateTime.now();
    }

    public static SuspensionHistory create(Long userId, Long reportId, Long adminId,
                                           SuspensionType suspensionType, String reason,
                                           LocalDateTime startAt, LocalDateTime endAt) {
        return new SuspensionHistory(userId, reportId, adminId, suspensionType, reason, startAt, endAt);
    }
}
