package consome.application.admin;

import consome.application.admin.result.ManageTreeResult;
import consome.domain.admin.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ManageFacade {
    private final BoardService boardService;
    private final CategoryService categoryService;
    private final SectionService sectionService;

    public ManageTreeResult getTree() {
        var sections = sectionService.findAllOrdered();
        var boards = boardService.findAllOrdered();
        var categories = categoryService.findAllOrdered();

        var boardsBySection = boards.stream()
                .collect(Collectors.groupingBy(Board::getSectionId));
        var catsByBoard = categories.stream()
                .collect(Collectors.groupingBy(Category::getBoardId));

        var sectionNodes = sections.stream()
                .map(section -> new ManageTreeResult.SectionNode(
                        section.getId(),
                        section.getName(),
                        section.getDisplayOrder(),
                        boardsBySection.getOrDefault(section.getId(), List.of()).stream()
                                .map(board -> new ManageTreeResult.BoardNode(
                                        board.getId(),
                                        board.getName(),
                                        board.getDisplayOrder(),
                                        catsByBoard.getOrDefault(board.getId(), List.of()).stream()
                                                .map(cat -> new ManageTreeResult.CategoryNode(
                                                        cat.getId(),
                                                        cat.getName(),
                                                        cat.getDisplayOrder()
                                                ))
                                                .toList()
                                ))
                                .toList()
                ))
                .toList();

        return new ManageTreeResult(sectionNodes);
    }
}
