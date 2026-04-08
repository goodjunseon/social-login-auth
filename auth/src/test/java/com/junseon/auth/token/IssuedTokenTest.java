package com.junseon.auth.token;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class IssuedTokenTest {

    @Test
    void shouldAllowNullRefreshToken() {
        IssuedToken token = new IssuedToken("access-token", null);

        assertThat(token.accessToken()).isEqualTo("access-token");
        assertThat(token.refreshToken()).isNull();
    }

    @Test
    void shouldRejectBlankRefreshTokenWhenProvided() {
        assertThatThrownBy(() -> new IssuedToken("access-token", " "))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("refreshToken must not be blank when provided");
    }
}
