package consome.interfaces.post.dto;

import consome.application.post.ImageUploadResult;

import java.util.List;

public record ImageUploadResponse(List<ImageInfo> images) {

    public record ImageInfo(String url, String originalName) {
    }

    public static ImageUploadResponse from(List<ImageUploadResult> results) {
        List<ImageInfo> images = results.stream()
                .map(r -> new ImageInfo(r.url(), r.originalName()))
                .toList();
        return new ImageUploadResponse(images);
    }
}
