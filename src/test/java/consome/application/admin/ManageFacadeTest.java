package consome.application.admin;

import consome.application.admin.result.ManageTreeResult;
import consome.domain.admin.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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

    @Test
    @DisplayName("findUsers – 첫 페이지 조회 시 20명씩, 총 1001명이 조회된다")
    void getUsers_firstPage() {
        // given
        int page = 0;
        int size = 20;
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").ascending());

        // when
        UserPagingResult result = manageFacade.getUsers(pageable);

        // then
        // 내용 검증
        List<UserRowResult> users = result.users(); // (클래스면 result.getContent()로 변경)

        assertThat(result).isNotNull();
        assertThat(users).hasSize(size);

        // 총 개수: 유저 1000 + 어드민 1
        assertThat(result.totalElements()).isEqualTo(1001L);   // (클래스면 getTotalElements())
        assertThat(result.page()).isEqualTo(page);             // (클래스면 getPage())
        assertThat(result.size()).isEqualTo(size);             // (클래스면 getSize())

        long expectedTotalPages = (long) Math.ceil(1001.0 / size);
        assertThat(result.totalPages()).isEqualTo(expectedTotalPages); // (클래스면 getTotalPages())

        // 한 건 내용 sanity check
        UserRowResult first = users.get(0);
        assertThat(first.userId()).isNotNull();          // (클래스면 getUserId())
        assertThat(first.loginId()).isNotBlank();        // (클래스면 getLoginId())
        assertThat(first.nickname()).isNotBlank();       // (클래스면 getNickname())
        assertThat(first.userPoint()).isNotNull();       // (클래스면 getUserPoint())
    }

    @Test
    @DisplayName("findUsers – 페이지를 넘기면 다른 유저들이 조회된다")
    void getUsers_secondPage_isDifferentFromFirstPage() {
        // given
        int size = 20;
        Pageable firstPage = PageRequest.of(0, size, Sort.by("id").ascending());
        Pageable secondPage = PageRequest.of(1, size, Sort.by("id").ascending());

        // when
        UserPagingResult firstResult = manageFacade.getUsers(firstPage);
        UserPagingResult secondResult = manageFacade.getUsers(secondPage);

        // then
        List<UserRowResult> firstContent = firstResult.users();
        List<UserRowResult> secondContent = secondResult.users();

        assertThat(firstContent).hasSize(size);
        assertThat(secondContent).hasSize(size);

        // 첫 페이지 첫 번째 유저와 두 번째 페이지 첫 번째 유저는 id가 달라야 한다
        Long firstId = firstContent.get(0).userId();
        Long secondId = secondContent.get(0).userId();

        assertThat(firstId).isNotEqualTo(secondId);
    }
}
