package consome.interfaces.post.dto;

import consome.application.post.VideoUploadResult;

import java.util.List;

public record VideoUploadResponse(List<VideoInfo> videos) {

    public record VideoInfo(String url, String originalName) {
    }

    public static VideoUploadResponse from(List<VideoUploadResult> results) {
        List<VideoInfo> videos = results.stream()
                .map(r -> new VideoInfo(r.url(), r.originalName()))
                .toList();
        return new VideoUploadResponse(videos);
    }
}
