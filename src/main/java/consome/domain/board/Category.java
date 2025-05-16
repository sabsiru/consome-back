package consome.domain.board;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Category {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long sectionId;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private int displayOrder;

    @Column(nullable = false)
    private boolean deleted = false;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    private Category(Long sectionId, String name, int displayOrder) {
        this.sectionId = sectionId;
        this.name = name;
        this.displayOrder = displayOrder;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public static Category create(Long sectionId, String name, int displayOrder) {
        return new Category(sectionId, name, displayOrder);
    }

    public void rename(String newName) {
        this.name = newName;
        this.updatedAt = LocalDateTime.now();
    }

    public void changeOrder(int newOrder) {
        this.displayOrder = newOrder;
        this.updatedAt = LocalDateTime.now();
    }

    public void delete() {
        this.deleted = true;
        this.updatedAt = LocalDateTime.now();
    }
}