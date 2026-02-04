package consome.application.navigation;

import consome.domain.post.PopularityType;

public record PopularBoardCriteria(
        int boardLimit,
        int previewLimit,
        int days,
        PopularityType sortBy
) {
    public static PopularBoardCriteria defaults() {
        return new PopularBoardCriteria(6, 5, 7, PopularityType.COMPOSITE);
    }

    public PopularBoardCriteria {
        if (boardLimit <= 0) boardLimit = 6;
        if (previewLimit <= 0) previewLimit = 5;
        if (days <= 0) days = 7;
        if (sortBy == null) sortBy = PopularityType.COMPOSITE;
    }
}
