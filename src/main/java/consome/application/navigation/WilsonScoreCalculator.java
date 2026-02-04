package consome.application.navigation;

public class WilsonScoreCalculator {
    private static final double Z = 1.96;
    private static final double COMMENT_WEIGHT = 0.3;

    public static double calculate(int views, int likes, int comments) {
        if (views == 0) return 0;
        double positive = likes + (comments * COMMENT_WEIGHT);
        double n = views;
        double p = positive / n;

        double z2 = Z * Z;
        double pq = p * (1 - p);
        double denominator = 1 + z2 / n;
        double center = p + z2 / (2 * n);
        double offset = Z * Math.sqrt(pq / n + z2 / (4 * n * n));
        return (center - offset) / denominator;
    }
}
