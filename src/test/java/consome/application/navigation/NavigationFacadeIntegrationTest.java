package consome.application.navigation;

import consome.application.admin.BoardFacade;
import consome.application.admin.SectionFacade;
import consome.domain.admin.Board;
import consome.domain.admin.Section;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class NavigationFacadeIntegrationTest {

    @Autowired
    NavigationFacade mainFacade;

    @Autowired
    SectionFacade sectionFacade;

    @Autowired
    BoardFacade boardFacade;

    @Test
    void 섹션을_생성하면_displayOrder_순으로_조회된다() {
        // given
        IntStream.of(3, 1, 5, 2, 6, 4)
                .forEach(order -> sectionFacade.create("섹션" + order, order));

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
        Section section = sectionFacade.create("게임", 1);
        IntStream.of(3, 1, 5, 2, 6, 4)
                .forEach(order -> boardFacade.create(section.getId(), "게시판" + order, "설명" + order, order));

        // when
        List<Board> boards = mainFacade.getBoards(section.getId());

        // then
        assertThat(boards)
                .extracting(Board::getDisplayOrder)
                .containsExactly(1, 2, 3, 4, 5, 6);
    }
}