# PLANS.md

## 운영 규칙

- 이 문서는 `AGENT.md` 기준 실행 계획 문서다.
- 범위는 **Apple 로그인 완료까지**다. (Kakao는 확장 준비 상태까지만)
- PHASE 상태는 `Not Started / In Progress / Done`으로만 관리한다.
- 일정/담당자/스토리포인트는 이 문서에서 다루지 않는다.

## 프로젝트 고정값

- Spring Boot: `3.3.2`
- Java: `17`
- Database: `MySQL`
- JWT Library: `jjwt`
- Test DB 전략:
  - 1차: `H2` + `application-test`로 빠른 안정화
  - 2차: `Testcontainers(MySQL)`로 운영 정합성 검증

## PHASE 템플릿

아래 필드를 모든 PHASE에 동일하게 적용한다.

- Status
- Goal
- Entry Gate
- Tasks
- Definition of Done
- Risk & Mitigation
- Verification
- Outputs
- Planned API/Type Changes
- Exit Gate

## 공개 API/타입 변경 관리 원칙

- 이 계획 문서 자체는 런타임 API를 직접 변경하지 않는다.
- 다만 PHASE별로 예정된 API/타입 변경을 명시한다.
- 변경은 “요청/응답 DTO”, “토큰 응답 스키마”, “오류 응답 포맷” 중심으로 추적한다.

---

## PHASE 0. 기준선 정렬 (빌드/버전/설정 구조)

- Status: `Done`
- Goal: 현재 코드와 기술 결정(Spring Boot 3.3.2, MySQL, jjwt)을 일치시키고 설정 구조의 기준선을 만든다.
- Entry Gate:
  - `AGENT.md`의 기술 결정이 확정되어 있다.
  - 현재 저장소에서 빌드 파일과 설정 파일 위치를 확인할 수 있다.
- Tasks:
  - `build.gradle` 의존성과 버전을 결정사항에 맞게 정렬한다.
  - 설정 파일 구조(`application.yml`, `application-*.yml`) 도입 계획을 확정한다.
  - 민감정보 외부화 목록을 환경변수 키 기준으로 정의한다.
  - 기존 `application.properties`를 단계적으로 대체할 마이그레이션 순서를 정의한다.
- Definition of Done:
  - 빌드/버전/설정 구조 기준이 문서와 코드에서 충돌하지 않는다.
  - 환경별 설정 분리 전략이 문서화되어 있다.
- Risk & Mitigation:
  - Risk: 버전 정렬 중 의존성 충돌 발생.
  - Mitigation: 최소 의존성 세트부터 빌드 확인 후 단계적으로 추가.
- Verification:
  - `./gradlew tasks`
  - `./gradlew dependencies` (필요 시)
  - 설정 파일 로딩 우선순위 체크리스트 확인
- Outputs:
  - 기준선 정렬된 빌드 설정
  - 설정 분리 기준 문서(키/프로필)
- Planned API/Type Changes:
  - 없음 (내부 설정/빌드 기준선 작업)
- Exit Gate:
  - 다음 PHASE에서 테스트 환경(H2)을 즉시 구성할 수 있다.
  - `application.properties` 마이그레이션 순서가 문서에 명시되어 있다.

## PHASE 1. 테스트 기반 복구 (`contextLoads` 통과)

- Status: `Done`
- Goal: 테스트 기본선을 복구해 `./gradlew test`가 통과하는 상태를 만든다.
- Entry Gate:
  - PHASE 0 완료
  - 테스트 실패 원인이 DataSource 미구성임이 확인되어 있다.
- Tasks:
  - `application-test` 프로필을 도입한다.
  - H2 in-memory 설정을 추가한다.
  - 테스트 시 불필요한 외부 연동 Bean을 분리/비활성화한다.
  - 최소 smoke test(`contextLoads`)를 안정화한다.
- Definition of Done:
  - `./gradlew test`가 로컬에서 통과한다.
  - 테스트 실행이 외부 DB 없이 재현 가능하다.
- Risk & Mitigation:
  - Risk: 운영 설정이 테스트 프로필에 유입.
  - Mitigation: `@ActiveProfiles("test")` 및 프로필 분리 검증.
- Verification:
  - `./gradlew clean test`
  - 테스트 리포트에서 실패 0건 확인
- Outputs:
  - `application-test` 기반 테스트 설정
  - 안정화된 최소 테스트 스위트
- Planned API/Type Changes:
  - 없음 (테스트 인프라 작업)
- Exit Gate:
  - 다음 PHASE에서 공통 응답/예외/보안 골격 추가 시 회귀 검증이 가능하다.

## PHASE 2. 공통 기반 구축 (설정/응답/예외/Security 골격)

- Status: `Done`
- Goal: 이후 기능 개발의 공통 기반을 먼저 고정한다.
- Entry Gate:
  - PHASE 1 완료 (`./gradlew test` 통과 상태)
- Tasks:
  - `@ConfigurationProperties` 기반 설정 클래스를 도입한다.
  - 공통 API 응답 포맷을 정의한다.
  - `ErrorCode`와 `GlobalExceptionHandler`를 도입한다.
  - Security 최소 골격(인가 경로, 실패 핸들러, stateless 방향)을 정의한다.
- Definition of Done:
  - 응답/예외 포맷 일관성이 보장된다.
  - 인증/인가 실패 응답이 공통 형식으로 반환된다.
- Risk & Mitigation:
  - Risk: 공통 포맷 과설계로 초기 개발 지연.
  - Mitigation: Apple 로그인에 필요한 최소 필드만 먼저 고정.
- Verification:
  - `2026-04-08` 기준 `./gradlew test` 통과
  - 예외/인증 실패 케이스 응답 검증 테스트(`COMMON_001`, `COMMON_999`, `SECURITY_001`, `SECURITY_002`) 통과
  - 공개/보호 경로 검증(`GET /actuator/health` 공개, `GET /api/v1/secure/ping` 비인증 401, `GET /api/v1/admin/ping` 권한 부족 403) 통과
- Outputs:
  - `@ConfigurationProperties` 기반 `auth.*` 타입 바인딩(`jwt`, `apple`, `kakao`)
  - 공통 성공 응답 래퍼 `ApiResponse<T> { success, code, message, data }`
  - 공통 오류 응답 `ErrorResponse { success, code, message, path, timestamp }`
  - `ErrorCode` + `GlobalExceptionHandler` 기반 공통 예외 처리
  - Security 기본 구성(stateless, 인증/인가 실패 핸들러 분리, 공개 경로 정책 반영)
- Planned API/Type Changes:
  - 공통 성공 응답 스키마 확정: `ApiResponse<T> { success, code, message, data }`
  - 공통 오류 응답 스키마 확정: `ErrorResponse { success, code, message, path, timestamp }`
  - 보안 실패(401/403) 응답도 동일 오류 스키마로 통일
- Exit Gate:
  - 도메인/Provider 계약을 얹을 공통 런타임 규칙이 준비되어 있다.

## PHASE 3. 도메인 계약 고정 (`social` 공통 인터페이스 + 책임 경계)

- Status: `Done`
- Goal: Apple 구현 전에 다중 Provider 확장 가능한 도메인 계약을 최소 수준으로 고정한다.
- Entry Gate:
  - PHASE 2 완료
- Tasks:
  - `SocialProvider`, `SocialUserInfo`, `SocialLoginCommand`, `SocialIdentityVerifier` 계약을 정의한다.
  - `auth`, `user`, `token`, `social` 책임 경계를 코드 수준으로 분리한다.
  - Apple 전용 DTO/검증 로직이 공통 계층에 침투하지 않도록 경계 규칙을 정리한다.
- Definition of Done:
  - Apple 구현 없이도 Provider 확장 포인트가 드러난다.
  - 계층 책임 혼합(Auth/User/Token/Social)이 제거된다.
- Risk & Mitigation:
  - Risk: 추상화 과다로 불필요한 복잡도 증가.
  - Mitigation: Apple 구현에 필요한 인터페이스만 최소 정의.
- Verification:
  - `2026-04-08` 기준 `./gradlew test` 통과
  - 계약 단위 테스트 통과(`SocialContractTest`)
  - 아키텍처 규칙 테스트 통과(`ArchitectureRulesTest`)
- Outputs:
  - `social` 루트 공통 계약 타입(`SocialProvider`, `SocialLoginCommand`, `SocialUserInfo`, `SocialIdentityVerifier`)
  - `social.apple` 구현 스켈레톤(`AppleSocialIdentityVerifier`)
  - 오케스트레이션 스켈레톤(`SocialLoginUseCase`, `UserIdentityService`, `TokenIssuer`, `IssuedToken`)
  - 계층 책임 경계 문서(`docs/phase3-boundary-rules.md`)
  - 의존 방향 강제용 ArchUnit 테스트(`ArchitectureRulesTest`)
- Planned API/Type Changes:
  - 내부 로그인 커맨드 타입 도입
  - 내부 소셜 사용자 정보 타입 도입
- Exit Gate:
  - PHASE 4에서 Apple 구현을 공통 계약 기반으로 시작할 수 있다.

## PHASE 4. Apple 검증 파이프라인 구현

- Status: `Not Started`
- Goal: Apple identity token 검증의 핵심 파이프라인을 구현한다.
- Entry Gate:
  - PHASE 3 완료
  - Apple 설정값(client-id/team-id/key-id/private-key/issuer/audience) 구조가 준비되어 있다.
- Tasks:
  - Apple JWK 조회 클라이언트를 구현한다.
  - JWT 헤더/클레임 파싱 및 서명 검증을 구현한다.
  - `iss`, `aud`, `exp`, `nonce` 검증을 구현한다.
  - 검증 성공 시 `sub`를 추출해 공통 `SocialUserInfo`로 매핑한다.
  - 검증 실패 케이스를 공통 `ErrorCode`로 연결한다.
- Definition of Done:
  - Apple 토큰 유효/무효 시나리오가 안정적으로 분기된다.
  - Apple 전용 로직이 `social.apple`로 격리된다.
- Risk & Mitigation:
  - Risk: JWK 키 회전/네트워크 변동으로 검증 불안정.
  - Mitigation: 캐싱 전략 TODO를 남기고 재시도/예외 변환 정책을 명시.
- Verification:
  - `./gradlew test`
  - Apple verifier 단위 테스트(유효 토큰/서명 실패/aud 불일치/만료) 통과
- Outputs:
  - Apple verifier, JWK client, Apple 매핑 로직
  - Apple 검증 실패 코드 매핑
- Planned API/Type Changes:
  - Apple 로그인 요청 DTO 초안
  - Apple 검증 결과 타입(내부 전용) 도입
- Exit Gate:
  - PHASE 5에서 사용자 식별 및 JWT 발급 연동이 가능하다.

## PHASE 5. 로그인 오케스트레이션 (사용자 식별/가입 + JWT 발급)

- Status: `Not Started`
- Goal: Apple 검증 결과를 내부 사용자 인증 흐름으로 연결해 실제 로그인 완료 상태를 만든다.
- Entry Gate:
  - PHASE 4 완료
  - user/token 계층 책임 분리가 유지되고 있다.
- Tasks:
  - `sub + provider` 기준 사용자 식별/생성 로직을 구현한다.
  - 신규/기존 사용자 분기 규칙을 명확히 한다.
  - `jjwt` 기반 Access Token 발급 로직을 구현한다.
  - Refresh Token 필요 여부를 정책으로 명시하고 최소 전략을 반영한다.
- Definition of Done:
  - 로그인 성공 시 내부 토큰이 반환된다.
  - 사용자 신규 가입/재로그인 흐름이 일관되게 동작한다.
- Risk & Mitigation:
  - Risk: 사용자 식별 키 설계 오류로 중복 계정 발생.
  - Mitigation: provider + providerUserId 유니크 전략을 명시하고 테스트 추가.
- Verification:
  - `./gradlew test`
  - 신규 사용자 로그인/기존 사용자 로그인 시나리오 테스트 통과
- Outputs:
  - 로그인 오케스트레이션 서비스
  - JWT 발급 컴포넌트
  - 사용자 식별/가입 규칙
- Planned API/Type Changes:
  - 토큰 응답 스키마(v1) 정의
  - 내부 인증 결과 DTO 정의
- Exit Gate:
  - PHASE 6에서 외부 API로 기능을 노출할 준비가 완료된다.

## PHASE 6. API + 테스트 완성

- Status: `Not Started`
- Goal: 외부 로그인 API를 완성하고 핵심 성공/실패 시나리오 테스트를 마무리한다.
- Entry Gate:
  - PHASE 5 완료
- Tasks:
  - Apple 로그인 엔드포인트를 공개한다.
  - 요청/응답 DTO 검증 및 Validation 오류 응답을 정렬한다.
  - 통합 테스트(성공/실패 분기)를 작성한다.
  - 보안/예외 응답 포맷 일관성을 점검한다.
- Definition of Done:
  - API 레벨에서 Apple 로그인이 동작한다.
  - 실패 시나리오가 공통 에러 포맷으로 반환된다.
- Risk & Mitigation:
  - Risk: 컨트롤러/서비스 간 DTO 변환 중 책임 혼합.
  - Mitigation: presentation DTO와 domain DTO를 분리 유지.
- Verification:
  - `./gradlew test`
  - 로그인 API 성공/토큰검증실패/audience불일치/validation실패 테스트 통과
- Outputs:
  - 로그인 API 엔드포인트
  - API/통합 테스트 세트
- Planned API/Type Changes:
  - 외부 로그인 요청/응답 DTO 확정
  - 공통 오류 코드 노출 범위 확정
- Exit Gate:
  - Apple 로그인 완료 기준(AGENT.md DoD)을 충족한다.

## PHASE 7. 안정화/백로그 (Kakao 확장 준비 + Swagger 시점 결정)

- Status: `Not Started`
- Goal: Apple 완료 후 안정화를 수행하고 다음 확장 착수 조건을 고정한다.
- Entry Gate:
  - PHASE 6 완료
- Tasks:
  - Kakao 추가 시 필요한 확장 포인트를 점검한다.
  - Testcontainers(MySQL) 통합 테스트를 추가한다.
  - Swagger(OpenAPI) 도입 타이밍을 확정하고 TODO를 상태화한다.
  - 문서(AGENT/README/PLANS) 간 불일치 항목을 정리한다.
- Definition of Done:
  - Kakao 확장 착수 시 구조 변경 없이 구현 가능한 상태다.
  - 운영 DB 정합성 테스트(Testcontainers) 기준이 확보된다.
- Risk & Mitigation:
  - Risk: Apple 구현체에 Provider 종속 코드가 누적됨.
  - Mitigation: 공통 계약 기반 리팩터링 체크리스트 수행.
- Verification:
  - `./gradlew test`
  - Testcontainers 기반 MySQL 통합 테스트 통과
  - 문서 간 결정사항 일치성 체크 완료
- Outputs:
  - Kakao 확장 준비 체크리스트
  - Swagger 도입 결정 기록
  - 문서 동기화 결과
- Planned API/Type Changes:
  - 없음 (확장 준비/안정화 중심)
- Exit Gate:
  - Apple 완료 후 다음 릴리즈 계획(Kakao)으로 전환 가능하다.

---

## 문서 품질 체크리스트

- 모든 PHASE에 `Definition of Done`과 `Verification`이 존재한다.
- PHASE 간 `Entry Gate`와 `Exit Gate`가 논리적으로 연결된다.
- 테스트 기준은 `./gradlew test`를 공통 최소 기준으로 유지한다.
- Apple 핵심 실패 시나리오(토큰 검증 실패, audience 불일치, 신규/기존 사용자 분기)가 계획에 반영되어 있다.
- 보안/예외 응답 포맷 일관성 검증이 포함되어 있다.
