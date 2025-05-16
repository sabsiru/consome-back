package consome.domain.board;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class SectionTest {

    @Test
    void 섹션_생성_성공_tester() {
        // given
        String name = "게임";
        int order = 1;

        // when
        Section section = Section.create(name, order);

        // then
        assertThat(section.getName()).isEqualTo(name);
        assertThat(section.getDisplayOrder()).isEqualTo(order);
        assertThat(section.isDeleted()).isFalse();
        assertThat(section.getCreatedAt()).isNotNull();
        assertThat(section.getUpdatedAt()).isNotNull();
    }

    @Test
    void 섹션_이름_변경_성공_tester() {
        Section section = Section.create("게임", 1);

        section.rename("유머");

        assertThat(section.getName()).isEqualTo("유머");
    }

    @Test
    void 섹션_정렬순서_변경_성공_tester() {
        Section section = Section.create("게임", 1);

        section.changeOrder(5);

        assertThat(section.getDisplayOrder()).isEqualTo(5);
    }

    @Test
    void 섹션_삭제_성공_tester() {
        Section section = Section.create("게임", 1);

        section.delete();

        assertThat(section.isDeleted()).isTrue();
    }
}