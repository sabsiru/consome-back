package consome.domain.board;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Board {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long categoryId;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private int displayOrder;

    @Column(nullable = false)
    private boolean deleted = false;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    private Board(Long categoryId, String name, String description, int displayOrder) {
        this.categoryId = categoryId;
        this.name = name;
        this.description = description;
        this.displayOrder = displayOrder;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public static Board create(Long categoryId, String name, String description, int displayOrder) {
        return new Board(categoryId, name, description, displayOrder);
    }

    public void rename(String newName) {
        this.name = newName;
        this.updatedAt = LocalDateTime.now();
    }

    public void changeOrder(int newOrder) {
        this.displayOrder = newOrder;
        this.updatedAt = LocalDateTime.now();
    }

    public void changeDescription(String newDescription) {
        this.description = newDescription;
        this.updatedAt = LocalDateTime.now();
    }

    public void delete() {
        this.deleted = true;
        this.updatedAt = LocalDateTime.now();
    }
}