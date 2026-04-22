package consome.interfaces.statistics.dto;

import consome.application.statistics.PopularKeywordResult;

import java.util.List;

public record PopularKeywordsResponse(
        String period,
        int limit,
        List<Item> items
) {
    public record Item(int rank, String keyword, long count) {}

    public static PopularKeywordsResponse of(String period, int limit, List<PopularKeywordResult> results) {
        List<Item> items = results.stream()
                .map(r -> new Item(r.rank(), r.keyword(), r.count()))
                .toList();
        return new PopularKeywordsResponse(period, limit, items);
    }
}
