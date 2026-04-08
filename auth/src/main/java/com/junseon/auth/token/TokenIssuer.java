package com.junseon.auth.token;

public interface TokenIssuer {

    IssuedToken issue(Long userId);
}
