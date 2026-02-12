# 🎮 CONSOME

> **CONSOLE + MOBILE** — 모든 게임을 아우르는 통합 게임 커뮤니티 백엔드

[![Java](https://img.shields.io/badge/Java-17-orange?logo=openjdk)](https://openjdk.org/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.4.5-brightgreen?logo=springboot)](https://spring.io/projects/spring-boot)
[![MySQL](https://img.shields.io/badge/MySQL-8.0-blue?logo=mysql&logoColor=white)](https://www.mysql.com/)

---

## 📖 소개

콘솔, 모바일, PC까지 — 모든 플랫폼의 게임을 아우르는 통합 커뮤니티입니다.
마이너한 게임들의 정보를 한 곳에서 모아 볼 수 있습니다.

## 🛠 기술 스택

| Category | Stack |
|----------|-------|
| **Language** | Java 17 |
| **Framework** | Spring Boot 3.4.5, Spring Security |
| **ORM** | Spring Data JPA, QueryDSL 5.0.0 |
| **Database** | MySQL 8.0, Redis 7 |
| **Auth** | JWT |
| **Test** | JUnit 5, Mockito, Testcontainers |

---

## 🏗 아키텍처

**4-레이어드 아키텍처** + **Facade 패턴**

```
┌─────────────────────────────────────────────────────────┐
│  interfaces/     Controller, Request/Response DTO       │
├─────────────────────────────────────────────────────────┤
│  application/    Facade, Criteria/Result (유스케이스)    │
├─────────────────────────────────────────────────────────┤
│  domain/         Entity, Service, Repository, Exception │
├─────────────────────────────────────────────────────────┤
│  infrastructure/ QueryDSL 구현체, Config, Security      │
└─────────────────────────────────────────────────────────┘
```

**핵심 흐름**: `Controller` → `Facade` → `Service` → `Repository`

---

## 📁 디렉토리 구조

```
src/main/java/consome/
├── 📂 application/
│   ├── admin/          # AdminDashboardFacade, AdminBoardFacade
│   ├── board/          # BoardFacade
│   ├── comment/        # CommentFacade
│   ├── navigation/     # NavigationFacade
│   ├── post/           # PostFacade
│   └── user/           # UserFacade
│
├── 📂 domain/
│   ├── admin/          # Board, Category, BoardManager
│   ├── auth/           # PasswordPolicy
│   ├── comment/        # Comment, CommentStat, CommentReaction
│   │   └── exception/  # CommentException
│   ├── common/
│   │   └── exception/  # BusinessException (범용)
│   ├── level/          # UserLevel, LevelInfo
│   ├── point/          # Point, PointHistory
│   ├── post/           # Post, PostStat, PostReaction, PostView
│   │   └── exception/  # PostException
│   └── user/           # User, Role
│       └── exception/  # UserException
│
├── 📂 infrastructure/
│   ├── */              # QueryRepositoryImpl 구현체들
│   ├── config/         # 설정
│   ├── jwt/            # JWT 처리
│   └── security/       # Spring Security
│
└── 📂 interfaces/
    ├── advice/         # GlobalExceptionHandler
    ├── error/          # ErrorResponse
    └── */v1/           # REST Controllers
```

---

## 🗃 도메인 모델

```
Section ─┬─ Board ─┬─ Category
         │         └─ Post ──── Comment
         └─ BoardManager

User ──── Point ──── PointHistory
     └─── UserLevel
```

| 도메인 | 엔티티 | 설명 |
|--------|--------|------|
| **Post** | Post, PostStat, PostReaction, PostView, PostImage | 게시글 + 통계 + 반응 |
| **Comment** | Comment, CommentStat, CommentReaction | 대댓글 (ref/step/depth) |
| **User** | User, Role | 사용자 (USER/ADMIN/MANAGER) |
| **Level** | UserLevel, LevelInfo | 레벨 시스템 (1-20 경험치, 21-22 랭킹) |
| **Point** | Point, PointHistory | 포인트 시스템 |

---

## 🔗 API Endpoints

### 📝 Post `/api/v1/posts`
| Method | Endpoint | Description |
|:------:|----------|-------------|
| `POST` | `/` | 게시글 작성 |
| `GET` | `/{postId}` | 상세 조회 |
| `PUT` | `/{postId}` | 수정 |
| `DELETE` | `/{postId}` | 삭제 |
| `POST` | `/{postId}/like` | 👍 추천 |
| `POST` | `/{postId}/dislike` | 👎 비추천 |

### 💬 Comment `/api/v1/posts/{postId}/comments`
| Method | Endpoint | Description |
|:------:|----------|-------------|
| `GET` | `/` | 목록 (페이징) |
| `POST` | `/` | 작성 |
| `PUT` | `/{commentId}` | 수정 |
| `DELETE` | `/{commentId}` | 삭제 |

### 👤 User `/api/v1/users`
| Method | Endpoint | Description |
|:------:|----------|-------------|
| `POST` | `/` | 회원가입 |
| `POST` | `/login` | 로그인 |
| `GET` | `/me` | 내 정보 |

### 🗂 Board `/api/v1/boards`
| Method | Endpoint | Description |
|:------:|----------|-------------|
| `GET` | `/{boardId}/posts` | 게시글 목록 |
| `GET` | `/{boardId}/categories` | 카테고리 목록 |
| `GET` | `/search` | 게시판 검색 |

### ⚙️ Admin `/api/v1/admin`
| Method | Endpoint | Description |
|:------:|----------|-------------|
| `GET` | `/manage/tree` | 트리 구조 |
| `GET` | `/manage/users` | 사용자 목록 |
| `POST` | `/manage/users/{userId}/role` | 관리자 지정 |

---

## ⚠️ 예외 처리

도메인별 커스텀 예외 클래스를 사용합니다.

| Exception | Domain | Example |
|-----------|--------|---------|
| `UserException` | 사용자 | `NotFound`, `DuplicateLoginId` |
| `PostException` | 게시글 | `NotFound`, `Unauthorized`, `AlreadyLiked` |
| `CommentException` | 댓글 | `NotFound`, `AlreadyDeleted` |
| `BusinessException` | 범용 | `BoardNotFound`, `InvalidPassword` |

```java
// 패턴: abstract class + static inner class
public abstract class PostException extends RuntimeException {
    private final String code;

    public static class NotFound extends PostException {
        public NotFound(Long postId) {
            super("POST_NOT_FOUND", "게시글을 찾을 수 없습니다: " + postId);
        }
    }
}
```

`GlobalExceptionHandler`에서 HTTP 상태 코드로 매핑됩니다.

---

## 📏 코딩 컨벤션

### DTO 네이밍

| Layer | Purpose | Pattern | Example |
|-------|---------|---------|---------|
| interfaces | 요청 | `*Request` | `CreatePostRequest` |
| interfaces | 응답 | `*Response` | `PostDetailResponse` |
| application | 검색조건 | `*Criteria` | `PostSearchCriteria` |
| application | 응답 | `*Result` | `PostResult` |
| domain | 명령 | `*Command` | `CreatePostCommand` |

### 주요 규칙

- ✅ 소프트 삭제: `deleted` 플래그 + `@Where`
- ✅ void 메서드 지양
- ✅ 비관적 락: 카운터/포인트 변경 시 `*ForUpdate()` 메서드

---

## 🚀 시작하기

### Prerequisites

- Java 17+
- Docker & Docker Compose
- MySQL 8.0

### Database Setup

```bash
# Docker로 MySQL 실행
docker-compose up -d

# 접속 정보
# Host: localhost:13306
# Database: consome
# User/Password: consome/consome
```

### Build & Run

```bash
# 빌드
./gradlew build

# 실행
./gradlew bootRun

# 테스트
./gradlew test
```

---

## 📄 License

This project is for learning purposes.
