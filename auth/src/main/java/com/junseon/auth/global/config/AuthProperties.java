package com.junseon.auth.global.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

// prefix = "auth"는 application.yml 파일에서 auth로 시작하는 속성들을 매핑
// 예를 들어, auth.jwt.secret, auth.apple.clientId, auth.kakao.clientId
@ConfigurationProperties(prefix = "auth")
public record AuthProperties(
        Jwt jwt,
        Apple apple,
        Kakao kakao
) {
    public record Jwt(
            String secret,
            long accessTokenExpirationSeconds,
            long refreshTokenExpirationSeconds
    ) {
    }

    public record Apple(
            String clientId,
            String teamId,
            String keyId,
            String privateKey,
            String redirectUri,
            String issuer,
            String audience
    ) {
    }

    public record Kakao(
            String clientId,
            String clientSecret,
            String redirectUri
    ) {
    }
}
