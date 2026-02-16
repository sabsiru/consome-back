package consome.application.admin;

public record BoardSearchResult(
        Long id,
        String name,
        String description,
        int displayOrder,
        boolean isMain
) {
}
