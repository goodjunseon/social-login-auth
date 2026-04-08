package com.junseon.auth.user.application;

import com.junseon.auth.social.SocialUserInfo;

public interface UserIdentityService {

    Long resolveOrCreateUser(SocialUserInfo socialUserInfo);
}
