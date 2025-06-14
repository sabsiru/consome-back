# CONSOME 프로젝트(가칭)
 - **CONSOME**란? 
   - CONSOLE & MOBILE의 합성어로 콘솔게임과 모바일게임 커뮤니티를 지향합니다.
   - 마이너한 게임들의 정보를 한 곳에서 모아 볼 수 있는 커뮤니티를 지향합니다.
   - 프로젝트명은 가명으로 언제든 변경될 수 있습니다.
   - 학습을 위한 프로젝트이나 실제 서비스까지 발전하길 바랍니다.

## 프로젝트 지향 점
 - 4-레이어드 아키텍처
   - `interfaces`: API 요청/응답 DTO, API 컨트롤러
   - `application`: 서비스 로직, 명령(Command), 조회(Query) 객체, Facade
   - `domain`: 도메인 모델, 도메인 이벤트, 도메인 서비스
   - `infrastructure`: 외부 시스템 연동, 데이터베이스 접근
   
 - Facade 패턴
    - 각 도메인 서비스는 Facade 패턴을 사용하여 외부에 노출됩니다.
    - 각 도메인 서비스는 해당 도메인의 비즈니스 로직을 캡슐화하고, 외부에서는 Facade를 통해 접근합니다.

- TDD(테스트 주도 개발)
    - 각 도메인 서비스에 대한 단위 테스트를 작성하여, 비즈니스 로직의 정확성을 검증합니다.
    - Mockito를 사용하여 의존성 주입 및 Mock 객체를 활용한 테스트를 수행합니다.
  
- DDD(도메인 주도 설계)
  - 도메인 모델을 중심으로 설계하며, 각 도메인의 비즈니스 로직을 도메인 서비스에 구현합니다.
  - 도메인 이벤트를 통해 도메인 간의 상호작용을 처리합니다.
 
 
## 네이밍 규칙

- 계층별 DTO
    
    | 계층             | 데이터 성격/용도                | 네이밍 규칙                                     | 예시                                                   |
    |------------------|-------------------------------|--------------------------------------------|------------------------------------------------------|
    | Interfaces       | 요청(Request)                  | \*Request                                  | PointChargeRequest                                   |
    |                  | 응답(Response)                 | \*Response                                 | PointHistoryResponse                                 |
    | application      | 명령(Command)                  | \*Command                                  | PointChargeCommand                                   |
    |                  | 조회(Query)                    | \*Query                                    | UserSearchQuery                                      |
    | domain           | 도메인 간 데이터 전달           | 도메인용어+\*Line/\*Info/\*Snapshot/\*Summary 등 | OrderLine, UserInfo, AccountSnapshot, ProductSummary |
    | infrastructure   | 외부 시스템/DB/API 연동 데이터  | \*외부 시스템의 네이밍을 따름                          | PointProducer(kafka)                                 |

- 메서드 명
    - 도메인 서비스의 메서드는 해당 도메인의 비즈니스 로직을 표현하는 동사로 구성합니다.
      - 예시: `charge`, `cancel`, `findById`, `findAll`, `save`, `delete`
  
## 개발 환경
- Java 17
- Spring Boot 3.4.5
- Spring Data JPA
- MySQL 8.0
- Lombok
- JUnit 5
- Mockito
- 추후 언제든 변경될 수 있습니다.

## 기능 구현 현황
- []회원 가입
---