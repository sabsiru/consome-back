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
public class Board {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long sectionId;

    @Size(min = 1, max = 20)
    @Column(nullable = false, unique = true, length = 20)
    private String name;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false, unique = true)
    private int displayOrder;

    @Column(nullable = false)
    private boolean deleted = false;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    private Board(Long sectionId, String name, String description, int displayOrder) {
        this.sectionId = sectionId;
        this.name = name;
        this.description = description;
        this.displayOrder = displayOrder;
    }

    public static Board create(Long refSectionId, String name, String description, int displayOrder) {
        return new Board(refSectionId, name, description, displayOrder);
    }

    public void rename(String newName) {
        this.name = newName;
    }

    public void changeOrder(int newOrder) {
        this.displayOrder = newOrder;
    }

    public void changeDescription(String newDescription) {
        this.description = newDescription;
    }

    public void delete() {
        this.deleted = true;
    }
}