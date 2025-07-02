package consome.domain.board;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class SectionServiceIntegrationTest {

    @Autowired
    SectionService sectionService;

    @Autowired
    SectionRepository sectionRepository;

    @Test
    void 섹션을_생성하면_DB에_저장된다() {
        // given
        String name = "게임";
        int displayOrder = 1;

        // when
        Section section = sectionService.create(name, displayOrder);

        // then
        assertThat(section.getId()).isNotNull();
        assertThat(section.getName()).isEqualTo(name);
        assertThat(section.getDisplayOrder()).isEqualTo(displayOrder);
        assertThat(section.isDeleted()).isFalse();
    }

    @Test
    void 섹션을_삭제하면_isDeleted가_true로_변경된다() {
        // given
        Section section = sectionService.create("커뮤니티", 2);

        // when
        sectionService.delete(section.getId());

        // then
        Section deletedSection = sectionRepository.findById(section.getId()).orElseThrow();
        assertThat(deletedSection.isDeleted()).isTrue();
    }

    @Test
    void 존재하지_않는_ID로_삭제하면_예외가_발생한다() {
        // expect
        assertThatThrownBy(() -> sectionService.delete(999L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("잘못된 접근");
    }

    @Test
    void 중복된_name은_예외발생(){
        //given
        String name = "중복";
        int displayOrder = 1;
        sectionService.create(name, displayOrder);

        //when&then
        assertThatThrownBy(() -> sectionService.create(name, displayOrder))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("이미 존재하는 섹션 이름입니다.");
    }

    @Test
    void 섹션_이름_변경_성공(){
        //given
        String originalName = "섹션1";
        int displayOrder = 1;
        Section section = sectionService.create(originalName, displayOrder);

        //when
        String newName = "섹션1_변경";
        Section updatedSection = sectionService.rename(section.getId(), newName);

        //then
        assertThat(updatedSection.getName()).isEqualTo(newName);
    }

    @Test
    void name_을_수정시_중복되면_예외발생(){
        //given
        String name1 = "섹션1";
        String name2 = "섹션2";
        int displayOrder = 1;
        Section section1 = sectionService.create(name1, displayOrder);
        Section section2 = sectionService.create(name2, displayOrder + 1);

        //when&then
        assertThatThrownBy(() -> sectionService.rename(section1.getId(), name2))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("이미 존재하는 섹션 이름입니다.");

    }

    @Test
    void 섹션_정렬순서_변경_성공() {
        // given
        String name = "섹션1";
        int originalOrder = 1;
        Section section = sectionService.create(name, originalOrder);

        // when
        int newOrder = 3;
        Section updatedSection = sectionService.changeOrder(section.getId(), newOrder);

        // then
        assertThat(updatedSection.getDisplayOrder()).isEqualTo(newOrder);
    }

    @Test
    void 섹션_이름은_1자_이상_10자_이하로_입력해야한다() {
        // when & then
        assertThatThrownBy(() -> sectionService.create("", 1))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("섹션 이름은 1자 이상 10자 이하로 입력해야 합니다.");

        assertThatThrownBy(() -> sectionService.create(" ", 1))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("섹션 이름은 1자 이상 10자 이하로 입력해야 합니다.");

        assertThatThrownBy(() -> sectionService.create("12345678901", 1))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("섹션 이름은 1자 이상 10자 이하로 입력해야 합니다.");
    }

}