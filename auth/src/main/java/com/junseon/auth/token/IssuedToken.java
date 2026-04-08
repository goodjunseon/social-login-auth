package com.junseon.auth.token;

public record IssuedToken(
        String accessToken,
        String refreshToken
) {
    public IssuedToken {
        if (accessToken == null || accessToken.isBlank()) {
            throw new IllegalArgumentException("accessToken must not be blank");
        }
    }
}
