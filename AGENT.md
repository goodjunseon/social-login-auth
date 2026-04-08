# AGENT.md

## 1) 문서 목적

이 문서는 `social-login-auth` 프로젝트의 구현 기준 문서다.
목표는 Apple 로그인부터 시작해 Kakao/기타 Provider로 확장 가능한 인증 서버를 안정적으로 구축하는 것이다.

이 문서는 아래를 해결한다.
- 지금 당장 구현할 내용과 나중에 확장할 내용을 분리
- 기술 의사결정의 근거를 명시
- 에이전트/개발자가 같은 기준으로 코드를 수정하도록 통일

---

## 2) 현재 코드 기준 스냅샷 (2026-04-07)

실제 저장소 상태 기준:
- Build Tool: Gradle (`auth/gradlew`, wrapper `9.4.1`)
- Framework: Spring Boot `3.3.2`
- Language: Java `17`
- Group/Package root: `com.junseon.auth`
- Dependencies: WebMVC, Security, JPA, Lombok, MySQL Connector, jjwt
- 설정 파일: `application.yml`, `application-test.yml` 존재 (`application.properties` 미사용)
- 테스트 현황: `contextLoads()` 실패 (DataSource 설정 누락)

이 스냅샷은 코드 변경 시 함께 갱신한다.

---

## 3) 기술 의사결정 상태

### A. 확정

- Java 17 사용 유지
  - 이유: 현재 toolchain과 일치, 안정적 생태계
- Gradle 유지
  - 이유: 현재 프로젝트와 일치
- Spring Security 기반 인증/인가
  - 이유: 표준 생태계, 확장성 확보
- JPA 기반 사용자 저장소
  - 이유: 다중 Provider 사용자 매핑에 적합
- Lombok 사용 허용
  - 이유: 보일러플레이트 감소, 팀 내 일관성 유지 시 효율적

### B. 이번에 확정된 항목

- Spring Boot `3.3.2` 고정
  - 이유: LTS 기반 안정성, 라이브러리 호환성, 학습자료 풍부
- Database: MySQL 고정
  - 이유: 프로젝트 목표 대비 운영 단순성 및 범용성
- JWT 라이브러리: `jjwt` 고정
  - 이유: Spring 기반 구현 자료가 많고 초기 개발 속도에 유리
- Swagger(OpenAPI) 도입 시점은 추후 결정
  - 기준: Apple 로그인 안정화 이후 문서 자동화 필요도 평가

남은 보류 의사결정은 Swagger 도입 타이밍 1건이다.

---

## 4) 아키텍처 원칙

### 4.1 Apple 우선, Apple 종속 금지

금지:
- `AuthService`가 Apple 토큰 검증 세부 구현까지 직접 담당
- Apple DTO를 공통 DTO로 재사용

권장:
- 공통 흐름은 `social` 추상 계층
- Apple 구현은 `social/apple` 하위 격리

### 4.2 Provider 책임 분리

Provider 고유 책임은 반드시 구현체로 격리한다.
예:
- Apple: identity token 검증, JWK 조회, claims 파싱
- Kakao: access token 기반 사용자 정보 조회

### 4.3 인증 흐름과 사용자 도메인 분리

- 인증 오케스트레이션: `auth`
- 사용자 식별/조회/생성: `user`
- 내부 토큰 발급/검증: `token` 또는 `security/token`

### 4.4 설정은 타입 세이프 우선

- `@Value` 남발 금지
- `@ConfigurationProperties` 우선
- 민감정보 하드코딩 금지
- 환경별 분리(`local/dev/prod/test`) 유지

### 4.5 응답/예외 포맷 일관성

필수:
- 공통 응답 포맷
- `ErrorCode` 체계
- `GlobalExceptionHandler`
- Validation/인증/인가 실패 응답 일관화

### 4.6 Security는 최소-확장형

초기에는 단순하게 시작하되 아래는 고정한다.
- Stateless 여부를 명시적으로 선택
- 인가 정책(`permitAll`, `authenticated`) 문서화
- 인증 실패/인가 실패 핸들러 분리

---

## 5) 권장 패키지 구조 (현재 패키지명 기준)

```text
com.junseon.auth
├── global
│   ├── config
│   ├── exception
│   └── response
├── auth
│   ├── application
│   ├── presentation
│   └── dto
├── user
│   ├── domain
│   ├── application
│   └── infrastructure
├── social
│   ├── application
│   ├── domain
│   ├── dto
│   ├── apple
│   │   ├── application
│   │   ├── client
│   │   ├── dto
│   │   └── verifier
│   └── kakao
├── security
└── token
```

규칙:
- 새로운 Provider 코드는 반드시 `social/<provider>`에 위치
- 공통 계약 인터페이스는 `social` 루트에 위치

---

## 6) Apple 로그인 구현 체크리스트

구현 최소 단위:
- identity token 입력 처리
- JWT 헤더/클레임 파싱
- Apple 공개키 조회(JWK) 및 서명 검증
- `iss`, `aud`, `exp`, `nonce` 검증
- `sub` 추출 후 내부 사용자 식별
- 신규 사용자 생성/기존 사용자 로그인 분기
- 내부 Access Token(필요 시 Refresh Token) 발급

주의:
- Apple 이메일은 항상 존재하지 않음
- 최초 로그인 이후 name/email 재수신이 제한될 수 있음

---

## 7) Kakao 확장 대비 계약

초기부터 아래 계약은 유지한다.
- `SocialProvider`
- `SocialUserInfo`
- `SocialLoginCommand`
- `SocialIdentityVerifier` (또는 동등 역할 인터페이스)

원칙:
- 지금 필요한 만큼만 추상화
- 다만 Provider 추가 시 엔트리 포인트를 다시 뜯어고치지 않게 설계

---

## 8) 설정/보안 규칙

권장 설정 파일:
- `application.yml`
- `application-local.yml`
- `application-dev.yml`
- `application-prod.yml`
- `application-test.yml`

`application.properties` 마이그레이션 순서:
1. `application.properties`의 공통 키를 `application.yml`로 이동
2. 테스트 전용 키(H2/Mock 값)는 `application-test.yml`로 분리
3. 로컬/개발/운영 키를 `application-local.yml`, `application-dev.yml`, `application-prod.yml`로 분리
4. 하드코딩된 민감값을 환경변수 바인딩으로 전환
5. 최종 검증 후 `application.properties`를 제거하고 재도입 금지

외부 분리 대상:
- DB 접속정보
- JWT secret/만료시간
- Apple `client-id`, `team-id`, `key-id`, `private-key`, `redirect-uri`
- Apple 검증값(`issuer`, `audience`)
- Kakao `client-id`, `client-secret`, `redirect-uri` (추후)

---

## 9) 테스트 전략

현재 문제:
- `AuthApplicationTests.contextLoads()` 실패 원인: 테스트용 DataSource 미구성

즉시 조치 원칙:
- `application-test` + in-memory DB(H2) 또는 Testcontainers 중 하나를 빠르게 확정
- 최소 smoke test(context load) 통과를 CI 진입 조건으로 설정

단계별 테스트:
- 1단계: context load + 핵심 서비스 단위 테스트
- 2단계: Apple verifier 단위 테스트(JWK/claim 검증)
- 3단계: 로그인 흐름 통합 테스트

---

## 10) 코드 작성 규칙

- 생성자 주입 우선, 필드 주입 금지
- 클래스 단일 책임 유지
- 의미 없는 TODO 금지
- 근거 없는 라이브러리 추가 금지
- 공통 포맷(응답/예외) 위반 금지

좋은 TODO 예시:
- `TODO: Apple JWK 캐싱 TTL 정책 확정 필요`
- `TODO: Refresh Token 저장소(RDB/Redis) 결정 필요`

---

## 11) 작업 절차 (에이전트/개발자 공통)

모든 변경은 아래 순서를 따른다.
1. 현재 구조/제약 확인
2. 변경 목표 한 줄 요약
3. 수정 파일 목록 확정
4. 파일별 변경 수행
5. 테스트/빌드 검증
6. 남은 TODO와 후속 이슈 기록

---

## 12) 완료 기준 (Definition of Done)

다음 조건을 만족해야 기능 완료로 본다.
- Apple 로그인 핵심 경로가 동작
- 구조가 Apple 전용으로 고착되지 않음
- 공통 응답/예외 포맷이 일관됨
- 민감정보 하드코딩이 없음
- 최소 테스트가 통과함
- 다음 Provider(Kakao) 추가 경로가 문서/코드에 드러남

---

## 13) 테스트 DB 전략 결정

테스트 DB는 아래 2단계 전략을 권장한다.

1. 1차(지금): H2 기반 `application-test`로 빠르게 context load/서비스 테스트 안정화
2. 2차(Apple 로그인 핵심 경로 완성 직후): Testcontainers MySQL 통합 테스트 추가

이 전략으로 초기 개발 속도와 운영 DB 정합성을 동시에 확보한다.
