# GitHub Copilot Review Instructions

## 1) 문서 목적

이 문서는 `social-login-auth` 프로젝트에서 GitHub Copilot Review가 Pull Request를 검토할 때 따라야 할 기준이다.
기준 원문은 `AGENT.md`(스냅샷 날짜: 2026-04-07)이며, 본 문서는 해당 기준을 리뷰 실행 관점으로 압축한 운영 지침이다.

핵심 목표:
- Apple 우선 구현을 진행하되 Apple 전용 구조로 고착되는 것을 방지
- Provider 확장(Kakao 등) 가능성을 유지
- 보안/설정/테스트 기준을 PR 단계에서 조기 차단

## 2) 프로젝트 스냅샷 요약 (2026-04-07)

- Build Tool: Gradle (`auth/gradlew`, wrapper `9.4.1`)
- Framework: Spring Boot `3.3.2`
- Language: Java `17`
- Package root: `com.junseon.auth`
- 주요 의존성: Spring WebMVC, Spring Security, Spring Data JPA, Lombok, MySQL Connector, jjwt
- 설정 파일: `application.yml`, `application-test.yml`
- 테스트 현황: `contextLoads()` 실패 이력 존재 (테스트용 DataSource 구성 이슈)

주의: 스냅샷 정보가 코드와 달라지면 `AGENT.md` 기준을 우선 확인하고 리뷰 의견에 날짜를 명시한다.

## 3) 리뷰 우선순위

Copilot Review는 아래 순서로 우선 검토한다.

1. 아키텍처/책임 분리
2. 보안/설정
3. 테스트/품질 게이트

## 4) Must-Fix Before Merge (필수 차단 규칙)

아래 항목 위반 시 PR은 승인 불가로 판정한다.

### A. 아키텍처/책임 분리

- `AuthService`(또는 동등 오케스트레이션 계층)에 Apple 토큰 검증 세부 로직(JWK 조회, claims 검증 등)을 직접 구현하면 안 된다.
- Apple 전용 DTO/검증 로직을 공통 계층에 섞으면 안 된다.
- Provider 고유 책임은 `social/<provider>` 하위로 격리되어야 한다.
- 공통 계약 인터페이스(`SocialProvider`, `SocialUserInfo`, `SocialLoginCommand`, `SocialIdentityVerifier` 또는 동등 역할)는 공통 계층에 유지되어야 한다.

### B. 응답/예외 일관성

- 공통 응답 포맷을 우회하는 임의 응답 구조 추가 금지
- `ErrorCode` 체계 무시 금지
- `GlobalExceptionHandler`를 우회한 예외 응답 생성 금지

### C. 보안/설정

- 민감정보(DB, JWT secret, Apple/Kakao credential 등) 하드코딩 금지
- 설정 주입 시 `@Value` 남발 금지, `@ConfigurationProperties` 우선
- 환경 분리 원칙(`local/dev/prod/test`) 훼손 금지

## 5) Should-Fix (권장 개선 규칙)

아래 항목은 머지 전 보완을 강하게 권고한다.

- 생성자 주입 우선, 필드 주입 지양
- 클래스 단일 책임 원칙 유지
- 근거 없는 라이브러리 추가 지양
- 의미 없는 TODO 지양 (필요 시 의사결정 TODO로 명확히 작성)
- Apple 로그인 특성 반영 확인
  - 이메일 누락 가능성 처리
  - 최초 로그인 이후 name/email 재수신 제한 고려

## 6) 테스트/품질 게이트

리뷰 시 아래를 확인한다.

- 최소 smoke test인 `contextLoads()` 통과 가능 상태인지
- 변경 범위에 대한 테스트가 존재하는지
  - 단위 테스트: 서비스/검증 로직
  - 통합 테스트: 로그인 핵심 흐름(해당 변경에 영향이 있을 때)
- 테스트가 누락되었다면 누락 사유와 후속 일정이 PR 본문에 명시되어 있는지

참고 전략:
- 1차: H2 기반 `application-test` 안정화
- 2차: Testcontainers MySQL 통합 테스트 확장

## 7) Copilot Review 출력 형식

Copilot Review 코멘트는 아래 형식을 따른다.

- Severity: `P0` | `P1` | `P2`
- Category: `Architecture` | `Security` | `Test` | `Maintainability`
- Finding: 문제 설명 1~2문장
- Evidence: 관련 코드/설계 근거
- Suggested Fix: 바로 적용 가능한 수정 제안
- Impact: 미조치 시 영향 범위

심각도 기준:
- `P0`: 보안 취약점, 인증 실패 가능성, 머지 즉시 차단 필요
- `P1`: 구조 훼손/회귀 위험 높음, 머지 전 수정 필요
- `P2`: 개선 권고, 후속 작업 가능

## 8) 승인 가능 조건 체크리스트

Copilot Review는 최종 요약에 아래 체크리스트를 포함한다.

- [ ] Provider 책임 분리가 유지된다.
- [ ] `AuthService`가 Provider 세부 구현에 결합되지 않는다.
- [ ] 공통 응답/예외 포맷 일관성이 유지된다.
- [ ] 민감정보 하드코딩이 없다.
- [ ] 설정 바인딩/프로파일 분리 원칙이 지켜진다.
- [ ] 변경 영역 테스트가 존재하거나 누락 사유가 명시된다.

## 9) 범위 제한

- 본 문서는 Copilot Review 가이드만 다룬다.
- GitHub Actions, PR 템플릿, 브랜치 정책 자동화 연동은 본 범위에 포함하지 않는다.
