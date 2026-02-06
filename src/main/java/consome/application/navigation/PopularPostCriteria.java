package consome.application.navigation;

public record PopularPostCriteria(int limit) {
    public PopularPostCriteria {
        if (limit <= 0) limit = 20;
    }

    public static PopularPostCriteria defaults() {
        return new PopularPostCriteria(20);
    }
}
