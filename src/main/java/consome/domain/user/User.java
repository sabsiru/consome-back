package consome.domain.user;

import consome.domain.user.exception.UserException;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String loginId;

    @Column(nullable = false, unique = true)
    private String nickname;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    private User(String loginId, String nickname, String password) {
        this.loginId = loginId;
        this.nickname = nickname;
        this.password = password;
        this.role = Role.USER;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public static User create(String loginId, String nickname, String password) {
        return new User(loginId, nickname, password);
    }

    public static void validateLoginId(String loginId) {
        if (loginId.length() < 4 || loginId.length() > 20) {
            throw new UserException.InvalidLoginIdLength("로그인 아이디는 4자 이상 20자 이하로 입력해주세요.") {
            };
        }
        if (!loginId.matches("^[a-zA-Z0-9]+$")) {
            throw new UserException.InvalidLoginIdFormat("아이디는 영문 대소문자와 숫자만 포함해야 합니다.") {
            };
        }
    }

    public static void validateNickname(String nickname) {
        if (nickname.length() < 2 || nickname.length() > 20) {
            throw new UserException.InvalidNicknameLength("닉네임은 2자 이상 20자 이하로 입력해주세요.") {
            };
        }
        if (!nickname.matches("^[가-힣a-zA-Z0-9]+$")) {
            throw new UserException.InvalidNicknameFormat("닉네임은 한글, 영문 대소문자, 숫자만 포함해야 합니다.");
        }
    }

    public void changeNickname(String newNickname) {
        this.nickname = newNickname;
        this.updatedAt = LocalDateTime.now();
    }

    public void changePassword(String newPassword) {
        this.password = newPassword;
        this.updatedAt = LocalDateTime.now();
    }

    public void updateRole(Role newRole) {
        this.role = newRole;
        this.updatedAt = LocalDateTime.now();
    }
}