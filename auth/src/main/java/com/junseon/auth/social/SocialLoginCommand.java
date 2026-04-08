package com.junseon.auth.social;

import java.util.Objects;

public record SocialLoginCommand(
        SocialProvider provider,
        String token,
        String nonce
) {
    public SocialLoginCommand {
        Objects.requireNonNull(provider, "provider must not be null");
        validateNotBlank(token, "token");
    }

    private static void validateNotBlank(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(fieldName + " must not be blank");
        }
    }
}
