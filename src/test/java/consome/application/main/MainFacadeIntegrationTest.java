package consome.application.main;

import consome.application.admin.AdminFacade;
import consome.domain.board.*;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class MainFacadeIntegrationTest {

    @Autowired
    MainFacade mainFacade;

    @Autowired
    AdminFacade adminFacade;

    @Test
    void 섹션을_생성하면_displayOrder_순으로_조회된다() {
        // given
        IntStream.of(3, 1, 5, 2, 6, 4)
                .forEach(order -> adminFacade.createSection("섹션" + order, order));

        // when
        List<Section> sections = mainFacade.getSections();

        // then
        assertThat(sections)
                .extracting(Section::getDisplayOrder)
                .containsExactly(1, 2, 3, 4, 5, 6);
    }

    // board 조회
    @Test
    void 섹션에_속한_게시판들을_정렬순서대로_조회한다() {
        // given
        Section section = adminFacade.createSection("게임", 1);
        IntStream.of(3, 1, 5, 2, 6, 4)
                .forEach(order -> adminFacade.createBoard(section.getId(), "게시판" + order, "설명" + order, order));

        // when
        List<Board> boards = mainFacade.getBoards(section.getId());

        // then
        assertThat(boards)
                .extracting(Board::getDisplayOrder)
                .containsExactly(1, 2, 3, 4, 5, 6);
    }
}