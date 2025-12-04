package consome.application.admin;

import consome.application.admin.result.ManageTreeResult;
import consome.domain.admin.*;
import consome.domain.user.UserInfo;
import consome.domain.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ManageFacade {
    private final BoardService boardService;
    private final CategoryService categoryService;
    private final SectionService sectionService;
    private final UserService userService;

    public ManageTreeResult getTree() {
        List<Section> sections = sectionService.findAllOrdered();
        List<Board> boards = boardService.findAllOrdered();
        List<Category> categories = categoryService.findAllOrdered();

        Map<Long, List<Board>> boardsBySection = boards.stream()
                .collect(Collectors.groupingBy(Board::getSectionId));
        Map<Long, List<Category>> catsByBoard = categories.stream()
                .collect(Collectors.groupingBy(Category::getBoardId));

        List<ManageTreeResult.SectionNode> sectionNodes = sections.stream()
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

    public UserPagingResult getUsers(Pageable pageable) {
        Page<UserInfo> page = userService.findUsers(pageable);

        List<UserRowResult> content = page.getContent().stream()
                .map(userInfo -> new UserRowResult(
                        userInfo.userId(),
                        userInfo.loginId(),
                        userInfo.nickname(),
                        userInfo.role(),
                        userInfo.userPoint()
                ))
                .toList();

        return new UserPagingResult(
                content,
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages()
        );
    }
}
