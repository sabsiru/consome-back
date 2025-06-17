package consome.domain.user;

public enum Role {
    ADMIN("관리자"),
    USER("일반 사용자"),
    GUEST("게스트 사용자"),
    MANAGER("게시판 관리자");

    private final String description;
    Role(String description) {
        this.description = description;
    }

}
