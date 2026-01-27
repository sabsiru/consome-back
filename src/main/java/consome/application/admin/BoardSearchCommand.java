package consome.application.admin;

public record BoardSearchCommand(
        String keyword,
        Long id,
        String name
) {
}
