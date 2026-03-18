# 🎮 CONSOME

> **CONSOLE + MOBILE** — 대용량 트래픽을 고려한 고성능 통합 게임 커뮤니티 백엔드

[![Java](https://img.shields.io/badge/Java-17-orange?logo=openjdk)](https://openjdk.org/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.4.5-brightgreen?logo=springboot)](https://spring.io/projects/spring-boot)
[![MySQL](https://img.shields.io/badge/MySQL-8.0-blue?logo=mysql&logoColor=white)](https://www.mysql.com/)
[![Redis](https://img.shields.io/badge/Redis-7.0-red?logo=redis&logoColor=white)](https://redis.io/)

---

## 📖 프로젝트 소개

**CONSOME**은 콘솔, 모바일, PC 등 모든 플랫폼의 게임 정보를 아우르는 통합 커뮤니티 서비스입니다.

단순한 기능 구현을 넘어, 실제 운영 환경에서 발생할 수 있는 **대규모 트래픽 부하, 데이터 동시성 문제, 그리고 시스템 확장성**을 최우선으로 고려하여 설계된 백엔드 프로젝트입니다. **Layered Architecture와 DDD(Domain-Driven Design), Facade 패턴**을 적용하여 유지보수성이 뛰어나고 견고한 시스템을 구축하는 데 주력했습니다.

---

## 🚀 핵심 기술적 도전 및 아키텍처 설계 (Key Technical Challenges)

서비스의 안정성과 성능을 확보하기 위해 깊이 있게 고민하고 해결한 주요 기술적 내용입니다.

### 1. 공정하고 효율적인 '인기 게시글' 선정 시스템

대형 게시판의 글만 인기글로 선정되는 불공정함과, 실시간 집계로 인한 DB 부하 문제를 해결하기 위해 설계되었습니다.

#### A. 정규화된 상대적 스코어링 알고리즘

단순 합산이 아닌, 각 게시판의 평균 활동량 대비 상대적인 점수를 계산하여 공정성을 확보했습니다.

```
score = (viewCount / boardAvgViewCount) * 0.1   // 조회수 가중치 10%
      + (likeCount / boardAvgLikeCount) * 0.7   // 추천수 가중치 70% (가장 높음)
      + (commentCount / boardAvgCommentCount) * 0.2 // 댓글수 가중치 20%
```

- _게시판별 평균 통계(`boardAvg...`)는 실시간 부하를 줄이기 위해 매일 새벽 배치(Batch)로 갱신합니다._

#### B. Redis 기반의 실시간 처리 및 DB 이원화 워크플로우

1. **이벤트 발생:** 조회, 추천, 댓글 이벤트 발생 시 `PostFacade`를 통해 비동기로 점수 계산 요청.
2. **Redis 집계:** 계산된 점수를 **Redis Sorted Set** (`popular_post_candidates`)에 실시간 업데이트. 메모리 효율을 위해 **TTL(5일)** 설정.
3. **DB 승격(Promotion):** 일정 기준(`score >= 1.0 AND likeCount >= 5`)을 충족한 후보만 선별하여 메인 DB의 `popular_post` 테이블로 승격.
    - _결과: 실시간성은 Redis로 확보하고, 영구 저장 및 복잡한 조회가 필요한 데이터만 DB로 관리하여 부하를 분산했습니다._

<br>

### 2. 확장성 있는 '주요 게시판' 구조 및 고성능 캐싱 전략

메인 페이지와 같이 트래픽이 집중되는 영역의 성능을 극대화하고 유연한 운영이 가능하도록 설계했습니다.

- **하이브리드 운영 구조:**
  - **고정(Pinned) 게시판:** 관리자가 수동으로 지정하며, `displayOrder`를 통해 유연하게 순서를 제어합니다.
  - **인기(Popular) 게시판:** 최근 7일간의 데이터를 바탕으로 복합 점수(`COMPOSITE`)를 계산하여 자동으로 선정됩니다.
- **스레드 안전한 캐싱 적용 (`Cache Stampede` 방지):**
  빈번하게 조회되는 주요 게시판 목록에는 **`@Cacheable(sync = true)`**를 적용했습니다. 캐시가 만료되는 순간 다수의 요청이 동시에 DB로 몰리는 캐시 스탬피드 현상을 방지하고, 단 하나의 스레드만 DB에 접근하도록 하여 안정적인 응답 속도를 보장합니다.

<br>

### 3. 데이터 정합성을 위한 강력한 동시성 제어

포인트 적립, 게시글 추천과 같이 갱신 손실(Lost Update)이 치명적인 도메인에 대한 처리입니다.

- **비관적 락(Pessimistic Lock) 적용:** 포인트 히스토리 기록이나 조회수/추천수 카운터 증가 시 `SELECT ... FOR UPDATE` 기반의 비관적 락을 적용하여 동시 수정 요청을 순차적으로 안전하게 처리했습니다.
- **Testcontainers 기반 검증:** 실제 운영 환경과 동일한 MySQL Docker 컨테이너를 띄우고 멀티스레드 환경에서 동시성 테스트를 수행하여, 락 메커니즘이 정상 작동하고 데이터 정합성이 유지됨을 검증했습니다.

<br>

### 4. 안정적인 실시간 알림 시스템 (SSE)

- **SSE(Server-Sent Events)** 를 활용하여 댓글, 쪽지 도착 시 실시간 알림을 구현했습니다.
- 네트워크 불안정으로 연결이 끊길 경우를 대비해 클라이언트 측에 **Exponential Backoff(지수 백오프)** 알고리즘을 적용한 자동 재연결 로직을 구현하여 안정성을 높였습니다.

---

## 🛠 기술 스택 (Tech Stack)

| 분류              | 기술                                          | 비고                                  |
| ----------------- | --------------------------------------------- | ------------------------------------- |
| **Language**      | Java 17                                       |                                       |
| **Framework**     | Spring Boot 3.4.5, Spring Security            |                                       |
| **Architecture**  | **Layered Architecture, DDD, Facade Pattern** | 계층 간 책임 분리 및 도메인 중심 설계 |
| **Database**      | MySQL 8.0                                     | 메인 데이터 저장소                    |
| **NoSQL / Cache** | **Redis 7.0 (Master-Replica)**                | 인기글 후보, 세션/토큰, 캐싱 목적     |
| **ORM / Query**   | Spring Data JPA, **QueryDSL 5.0**             | 복잡한 동적 쿼리 처리                 |
| **Concurrency**   | **Pessimistic Lock**, Redis Distributed Lock  | 데이터 정합성 보장                    |
| **Test**          | JUnit 5, Mockito, **Testcontainers**          | 통합 테스트 및 동시성 검증 환경       |
| **Infra / Build** | Docker Compose, Gradle                        |                                       |

---

## 🏗 시스템 아키텍처

**4-레이어드 아키텍처**를 기반으로 **Facade 패턴**을 도입하여, 복잡한 유스케이스 흐름을 캡슐화하고 도메인 계층의 순수성을 유지했습니다.

```
[Client (Web/Mobile)]
       │ (REST API / SSE)
       ▼
┌─────────────────────────────────────────────────────────────┐
│  interfaces/ (Presentation Layer)                           │
│  - Controller, Request/Response DTO, Global Exception Handler │
├─────────────────────────────────────────────────────────────┤
│       │ (Command / Criteria)                                │
│       ▼                                                     │
│  application/ (Application Layer - Facade)                  │
│  - Facade: 여러 Service를 조합하여 트랜잭션 단위의 유스케이스 처리  │
├─────────────────────────────────────────────────────────────┤
│       │ (Call Domain Logic)                                 │
│       ▼                                                     │
│  domain/ (Domain Layer)                                     │
│  - Entity, Service(비즈니스 로직), Repository(Interface)       │
├─────────────────────────────────────────────────────────────┤
│       │ (Impl Interface / Use Infra)                        │
│       ▼                                                     │
│  infrastructure/ (Infrastructure Layer)                     │
│  - Repository Impl(QueryDSL), Redis, External API, Config   │
└─────────────────────────────────────────────────────────────┘
       │
       ▼
[MySQL] [Redis]
```

---

## 💡 주요 기능 요약

- **게시판 시스템:** 무한 계층형 카테고리 구조, 게시글 CRUD, 이미지 리사이징 및 업로드.
- **댓글 시스템:** 계층형 대댓글(답글) 지원.
- **게이미피케이션:** 활동 기반 포인트 적립 및 경험치 기반 레벨링 시스템.
- **쪽지 및 사용자 관리:** 1:1 쪽지, 사용자 차단/신고 기능.
- **관리자 시스템:** 게시판 구조 관리, 사용자 제재(정지), 신고 처리 등 통합 관리 기능.
- **보안 및 인증:** JWT Access/Refresh Token Rotation, 이메일 인증, Spring Security 기반 권한 관리.

---

## 📏 개발 컨벤션 및 원칙

- **DTO 네이밍:** 계층별/의도별로 명확히 분리 (`*Request`, `*Response`, `*Command`, `*Result`, `*Criteria`).
- **예외 처리:** 각 도메인별로 커스텀 예외(`PostException`, `UserException` 등)를 정의하고, 글로벌 핸들러에서 통일된 에러 응답 규격으로 처리합니다.
- **소프트 삭제(Soft Delete):** 데이터 복구 및 이력 관리를 위해 주요 엔티티에는 `deleted` 플래그를 사용하여 논리적 삭제를 적용했습니다.

---

## 🔢 프로젝트 규모

- **Backend:** Java 파일 약 380+개 (테스트 포함), 도메인 엔티티 18개
- **API:** 11개 도메인, 36개 이상의 REST API 엔드포인트
- **Scheduler:** 5종 (통계 집계, 알림 정리, 랭킹 산정, 정지 해제, 미사용 파일 정리)

---

## 🚀 시작하기 (Getting Started)

### Prerequisites

- Java 17+
- Docker & Docker Compose
- MySQL 8.0+

### Setup & Run

1. **인프라 실행 (MySQL, Redis):**

    ```bash
    docker-compose up -d
    ```

2. **애플리케이션 빌드 및 실행:**

    ```bash
    ./gradlew build
    ./gradlew bootRun
    ```
