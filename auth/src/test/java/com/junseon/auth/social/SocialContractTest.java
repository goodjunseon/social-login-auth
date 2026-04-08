package com.junseon.auth.social;

import com.junseon.auth.social.apple.AppleSocialIdentityVerifier;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class SocialContractTest {

    @Test
    void supportsShouldMatchProviderType() {
        AppleSocialIdentityVerifier verifier = new AppleSocialIdentityVerifier();

        assertThat(verifier.supports(SocialProvider.APPLE)).isTrue();
        assertThat(verifier.supports(SocialProvider.KAKAO)).isFalse();
    }

    @Test
    void socialLoginCommandShouldRejectNullOrBlankFields() {
        assertThatThrownBy(() -> new SocialLoginCommand(null, "token", "nonce"))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("provider must not be null");

        assertThatThrownBy(() -> new SocialLoginCommand(SocialProvider.APPLE, " ", "nonce"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("token must not be blank");

        assertThatThrownBy(() -> new SocialLoginCommand(SocialProvider.APPLE, "token", ""))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("nonce must not be blank");
    }

    @Test
    void socialUserInfoShouldAllowNullableOptionalFields() {
        SocialUserInfo userInfo = new SocialUserInfo(SocialProvider.APPLE, "apple-subject", null, null, null);

        assertThat(userInfo.provider()).isEqualTo(SocialProvider.APPLE);
        assertThat(userInfo.providerUserId()).isEqualTo("apple-subject");
        assertThat(userInfo.email()).isNull();
        assertThat(userInfo.name()).isNull();
        assertThat(userInfo.emailVerified()).isNull();
    }
}
