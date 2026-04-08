package com.junseon.auth.auth.application;

import com.junseon.auth.social.SocialLoginCommand;
import com.junseon.auth.token.IssuedToken;

public interface SocialLoginUseCase {

    IssuedToken login(SocialLoginCommand command);
}
