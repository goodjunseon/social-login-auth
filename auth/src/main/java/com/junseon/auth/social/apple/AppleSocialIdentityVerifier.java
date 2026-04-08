package com.junseon.auth.social.apple;

import com.junseon.auth.social.SocialIdentityVerifier;
import com.junseon.auth.social.SocialLoginCommand;
import com.junseon.auth.social.SocialProvider;
import com.junseon.auth.social.SocialUserInfo;
import org.springframework.stereotype.Component;

@Component
public class AppleSocialIdentityVerifier implements SocialIdentityVerifier {

    @Override
    public boolean supports(SocialProvider provider) {
        return SocialProvider.APPLE == provider;
    }

    @Override
    public SocialUserInfo verify(SocialLoginCommand command) {
        throw new UnsupportedOperationException("Apple token verification will be implemented in PHASE4");
    }
}
