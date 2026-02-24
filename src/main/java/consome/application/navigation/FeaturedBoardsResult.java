package consome.application.navigation;

import java.util.List;

public record FeaturedBoardsResult(
        List<BoardResult> pinnedBoards,
        List<BoardResult> popularBoards
) {}
