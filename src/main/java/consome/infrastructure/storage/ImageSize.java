package consome.infrastructure.storage;

public enum ImageSize {
    DISPLAY(800, 0.80f),       // 본문용: 800px, 품질 80%
    THUMBNAIL(150, 0.75f);     // 썸네일용: 150px, 품질 75%

    private final int maxDimension;
    private final float quality;

    ImageSize(int maxDimension, float quality) {
        this.maxDimension = maxDimension;
        this.quality = quality;
    }

    public int getMaxDimension() {
        return maxDimension;
    }

    public float getQuality() {
        return quality;
    }
}
