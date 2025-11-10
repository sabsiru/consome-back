package consome.domain.admin;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Getter
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Section {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Size(min = 1, max = 20)
    @Column(nullable = false, unique = true, length = 20)
    private String name;

    @Column(nullable = false, unique = true)
    private int displayOrder;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    @Column(nullable = false)
    private boolean deleted = false;

    private Section(String name, int displayOrder) {
        this.name = name;
        this.displayOrder = displayOrder;
    }

    public static Section create(String name, int displayOrder) {
        return new Section(name, displayOrder);
    }

    public void rename(String newName) {
        this.name = newName;
    }

    public void changeOrder(int newOrder) {
        this.displayOrder = newOrder;
    }

    public void delete() {
        this.deleted = true;
    }
}