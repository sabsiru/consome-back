package consome.application.navigation;

public record PopularPostCriteria(
        int limit,
        int days,
        int minViews
) {
    public PopularPostCriteria {
        if (limit <= 0) limit = 20;
        if (days <= 0) days = 3;
        if (minViews <= 0) minViews = 50;
    }

    public static PopularPostCriteria defaults() {
        return new PopularPostCriteria(20, 3, 50);
    }
}
