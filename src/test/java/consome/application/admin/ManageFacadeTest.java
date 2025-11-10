package consome.application.admin;

import consome.application.admin.result.ManageTreeResult;
import consome.domain.admin.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;


@SpringBootTest
@Transactional
class ManageFacadeTest {

    @Autowired
    private BoardService boardService;
    @Autowired
    private SectionService sectionService;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private ManageFacade manageFacade;

    @Test
    void getTreeTest() {
        // given
        //AdminInitializer

        // when
        ManageTreeResult tree = manageFacade.getTree();

        // then
        assertThat(tree.sections()).hasSize(1);

        ManageTreeResult.SectionNode sectionNode = tree.sections().get(0);
        assertThat(sectionNode.name()).isEqualTo("자유");
        assertThat(sectionNode.boards()).hasSize(1);

        // 자유게시판 검증
        ManageTreeResult.BoardNode freeBoard = sectionNode.boards().get(0);
        assertThat(freeBoard.name()).isEqualTo("자유게시판");
        assertThat(freeBoard.categories())
                .extracting(ManageTreeResult.CategoryNode::name)
                .containsExactly("잡담");

    }
}
