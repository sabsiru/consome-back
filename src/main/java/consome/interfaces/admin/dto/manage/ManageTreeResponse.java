package consome.interfaces.admin.dto.manage;

import consome.application.admin.result.ManageTreeResult;
import lombok.*;

import java.util.List;

@Getter
@AllArgsConstructor
public class ManageTreeResponse {
    private final List<Section> sections;

    @Getter @AllArgsConstructor
    public static class Section {
        private final Long id;
        private final String name;
        private final int displayOrder;
        private final List<Board> boards;
    }

    @Getter @AllArgsConstructor
    public static class Board {
        private final Long id;
        private final String name;
        private final String description;
        private final int displayOrder;
        private final List<Category> categories;
    }

    @Getter @AllArgsConstructor
    public static class Category {
        private final Long id;
        private final String name;
        private final int displayOrder;
    }

    public static ManageTreeResponse from(ManageTreeResult result) {
        List<Section> sectionDtos = result.sections().stream()
                .map(s -> new Section(
                        s.id(), s.name(), s.displayOrder(),
                        s.boards().stream()
                                .map(b -> new Board(
                                        b.id(), b.name(), b.description(),
                                        b.displayOrder(),
                                        b.categories().stream()
                                                .map(c -> new Category(c.id(), c.name(), c.displayOrder()))
                                                .toList()
                                ))
                                .toList()
                ))
                .toList();

        return new ManageTreeResponse(sectionDtos);
    }
}