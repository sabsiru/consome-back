package consome.domain.post;

public record BoardPopularityRow(
        Long boardId,
        String boardName,
        Double score
) {
}
