package com.junseon.auth.social;

import java.util.Objects;

public record SocialUserInfo(
        SocialProvider provider,
        String providerUserId,
        String email,
        String name,
        Boolean emailVerified
) {
    public SocialUserInfo {
        Objects.requireNonNull(provider, "provider must not be null");
        if (providerUserId == null || providerUserId.isBlank()) {
            throw new IllegalArgumentException("providerUserId must not be blank");
        }
    }
}
