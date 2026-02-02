# CONSOME

CONSOME (CONSOLE + MOBILE)은 마이너 콘솔/모바일 게임 커뮤니티를 위한 Spring Boot REST API 백엔드입니다.

## 프로젝트 소개

- 콘솔게임과 모바일게임 커뮤니티를 지향합니다.
- 마이너한 게임들의 정보를 한 곳에서 모아 볼 수 있는 커뮤니티입니다.
- 학습을 위한 프로젝트이나 실제 서비스까지 발전하길 바랍니다.

## 기술 스택

- Java 17
- Spring Boot 3.4.5
- Spring Data JPA
- QueryDSL 5.0.0
- Spring Security + JWT
- MySQL 8.0
- Testcontainers
- JUnit 5 / Mockito

## 아키텍처

4-레이어드 아키텍처를 사용합니다.

```
interfaces/    → Controller, Request/Response DTO
application/   → Facade, Criteria/Result (유스케이스)
domain/        → Entity, Service, Repository 인터페이스, Command/Info
infrastructure/→ QueryDSL 구현체, 설정
```

**핵심 패턴**: Controller → Facade → Service (컨트롤러는 Facade만 의존)

## 디렉토리 구조

```
src/main/java/consome/
├── application/
│   ├── admin/          # AdminDashboardFacade, AdminBoardFacade, AdminCategoryFacade, AdminStorageFacade
│   ├── board/          # BoardFacade
│   ├── comment/        # CommentFacade
│   ├── navigation/     # NavigationFacade
│   ├── post/           # PostFacade
│   └── user/           # UserFacade
├── domain/
│   ├── admin/
│   │   └── repository/ # BoardRepository, CategoryRepository 등
│   ├── auth/           # 인증
│   ├── comment/
│   │   └── repository/ # CommentRepository 등
│   ├── point/
│   │   └── repository/ # PointRepository 등
│   ├── post/
│   │   ├── entity/     # Post, PostStat, PostReaction, PostView, PostImage
│   │   └── repository/ # PostRepository, PostQueryRepository 등
│   └── user/
│       └── repository/ # UserRepository, UserQueryRepository
├── infrastructure/
│   ├── */              # QueryRepositoryImpl 구현체들
│   ├── config/         # 설정
│   ├── jwt/            # JWT 처리
│   └── security/       # Spring Security
└── interfaces/
    ├── admin/
    │   ├── dto/        # Request/Response DTO
    │   └── v1/         # AdminV1*Controller
    ├── board/
    │   └── v1/         # BoardV1Controller
    ├── comment/
    │   └── v1/         # CommentV1Controller
    ├── navigation/
    │   └── v1/         # NavigationV1Controller
    ├── post/
    │   └── v1/         # PostV1Controller
    └── user/
        └── v1/         # UserV1Controller
```

## 네이밍 규칙

### DTO 네이밍

| 계층 | 용도 | 네이밍 | 예시 |
|------|------|--------|------|
| interfaces | 요청 | `*Request` | `CreatePostRequest` |
| interfaces | 응답 | `*Response` | `PostDetailResponse` |
| application | 요청 (검색조건) | `*Criteria` | `PostSearchCriteria` |
| application | 응답 | `*Result` | `PostResult` |
| domain | 명령 | `*Command` | `CreatePostCommand` |
| domain | 응답 | `*Info` | `UserInfo` |

### Repository 규칙

- 위치: `domain/{도메인}/repository/`
- JpaRepository: `{Entity}Repository`
- QueryDSL: `{Entity}QueryRepository` (interface) + `{Entity}QueryRepositoryImpl` (infrastructure)

### Facade 규칙

- 사용자용: `{도메인}Facade` (예: `BoardFacade`, `PostFacade`)
- 관리자용: `Admin{도메인}Facade` (예: `AdminBoardFacade`, `AdminCategoryFacade`)
- 대시보드: `AdminDashboardFacade`

### 메서드명

- 도메인 서비스의 메서드는 비즈니스 로직을 표현하는 동사로 구성
- 예시: `charge`, `cancel`, `like`, `findById`, `save`, `delete`

## 도메인 구조

**계층**: Section → Board → Category → Post

### 주요 엔티티

| 도메인 | 엔티티 | 설명 |
|--------|--------|------|
| Post | Post, PostStat, PostReaction, PostView, PostImage | 게시글 + 통계 + 반응 + 조회 + 이미지 |
| Comment | Comment, CommentReaction, CommentStat | 댓글 (ref/step/depth 대댓글) |
| User | User, Role | 사용자 (USER/ADMIN/MANAGER) |
| Point | Point, PointHistory | 포인트 시스템 |
| Admin | Section, Board, Category, BoardManager | 게시판 관리 |

## API 엔드포인트

### Post (`/api/v1/posts`)
| Method | Endpoint | 기능 |
|--------|----------|------|
| POST | `/` | 게시글 작성 |
| POST | `/images` | 이미지 업로드 (5MB) |
| POST | `/videos` | 영상 업로드 (30MB) |
| GET | `/{postId}` | 상세 조회 + 조회수 |
| PUT | `/{postId}` | 수정 |
| DELETE | `/{postId}` | 삭제 |
| POST | `/{postId}/like` | 추천 |
| POST | `/{postId}/dislike` | 비추천 |

### Comment (`/api/v1/posts/{postId}/comments`)
| Method | Endpoint | 기능 |
|--------|----------|------|
| GET | `/` | 목록 (페이징) |
| POST | `/` | 작성 |
| PUT | `/{commentId}` | 수정 |
| DELETE | `/{commentId}` | 삭제 |
| POST | `/{commentId}/like` | 추천 |
| POST | `/{commentId}/dislike` | 비추천 |

### Board (`/api/v1/boards`)
| Method | Endpoint | 기능 |
|--------|----------|------|
| GET | `/{boardId}/posts` | 게시글 목록 |
| GET | `/{boardId}/categories` | 카테고리 목록 |
| GET | `/{boardId}` | 게시판 이름 |
| GET | `/search` | 게시판 검색 |

### User (`/api/v1/users`)
| Method | Endpoint | 기능 |
|--------|----------|------|
| POST | `/` | 회원가입 |
| POST | `/login` | 로그인 |
| GET | `/me` | 내 정보 |

### Navigation (`/api/v1/navigation`)
| Method | Endpoint | 기능 |
|--------|----------|------|
| GET | `/sections` | 섹션 목록 |
| GET | `/sections/{sectionId}/boards` | 게시판 목록 |
| GET | `/header` | 헤더용 트리 |

### Admin (`/api/v1/admin`)
| Method | Endpoint | 기능 |
|--------|----------|------|
| GET | `/manage/tree` | 트리 구조 |
| GET | `/manage/users` | 사용자 목록 |
| POST/DELETE | `/manage/users/{userId}/role` | 관리자 지정/해제 |
| DELETE | `/storage/orphan-files` | 고아 파일 정리 |

## 프로젝트 규칙

- 소프트 삭제: `deleted` 플래그 + `@Where`
- void 메서드 지양
- 파라미터 순서 통일

## 개발 환경

### 데이터베이스
MySQL 8.0 (Docker, 포트 13306)
- Database: `consome`
- User/Password: `consome/consome`

### 빌드 및 실행
```bash
./gradlew build
./gradlew bootRun
```

### 테스트
```bash
./gradlew test
```
