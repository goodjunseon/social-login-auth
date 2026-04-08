package com.junseon.auth.social;

public interface SocialIdentityVerifier {

    boolean supports(SocialProvider provider);

    SocialUserInfo verify(SocialLoginCommand command);
}
