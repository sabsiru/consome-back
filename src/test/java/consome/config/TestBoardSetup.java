package consome.config;

import consome.domain.admin.Board;
import consome.domain.admin.Category;
import consome.domain.admin.Section;
import consome.domain.admin.repository.BoardRepository;
import consome.domain.admin.repository.CategoryRepository;
import consome.domain.admin.repository.SectionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class TestBoardSetup {

    @Autowired
    private SectionRepository sectionRepository;

    @Autowired
    private BoardRepository boardRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    private Long sectionId;
    private Long boardId;
    private Long categoryId;
    private Long categoryId2;

    public void setup() {
        String suffix = UUID.randomUUID().toString().substring(0, 4);
        Section section = sectionRepository.save(Section.create("테스트섹션" + suffix));
        sectionId = section.getId();

        Board board = boardRepository.save(Board.create("테스트게시판" + suffix, "테스트용", sectionId));
        boardId = board.getId();

        Category cat1 = categoryRepository.save(Category.create(boardId, "공지사항", 1));
        categoryId = cat1.getId();

        Category cat2 = categoryRepository.save(Category.create(boardId, "자유", 2));
        categoryId2 = cat2.getId();
    }

    public Long getSectionId() { return sectionId; }
    public Long getBoardId() { return boardId; }
    public Long getCategoryId() { return categoryId; }
    public Long getCategoryId2() { return categoryId2; }
}
