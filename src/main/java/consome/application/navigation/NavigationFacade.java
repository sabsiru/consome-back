package consome.application.navigation;

import consome.domain.admin.Board;
import consome.domain.admin.BoardService;
import consome.domain.admin.Section;
import consome.domain.admin.SectionService;
import consome.domain.post.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NavigationFacade {
    private final SectionService sectionService;
    private final BoardService boardService;
    private final PostService postService;

    public List<Section> getSections() {
        return sectionService.findAllOrdered();
    }

    public List<SectionResult> getHeaderSections() {
        // 1. 섹션은 displayOrder 기준으로 정렬된 상태로 가져온다고 가정
        List<Section> sections = sectionService.findAllOrdered();

        // 2. 게시판도 displayOrder 기준으로 정렬해서 전부 가져온 뒤, 섹션별로 groupBy
        List<Board> boards = boardService.findAllOrdered();
        Map<Long, List<Board>> boardsBySectionId = boards.stream()
                .collect(Collectors.groupingBy(board -> board.getSectionId()));

        // 3. SectionResult 로 매핑
        return sections.stream()
                .map(section -> {
                    List<BoardResult> boardResults = boardsBySectionId
                            .getOrDefault(section.getId(), List.of())
                            .stream()
                            .map(board -> new BoardResult(
                                    board.getId(),
                                    board.getName(),
                                    board.getDisplayOrder()
                            ))
                            .toList();

                    return new SectionResult(
                            section.getId(),
                            section.getName(),
                            section.getDisplayOrder(),
                            boardResults
                    );
                })
                .toList();
    }

    public List<Board> getBoards(Long sectionId) {
        return boardService.findAllBySectionId(sectionId);
    }
}
