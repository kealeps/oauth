package com.oauth2.app.oauth.security;

import java.util.HashMap;
import java.util.Map;

import com.commons.users.userscommons.models.entity.User;
import com.oauth2.app.oauth.services.IUserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;
import org.springframework.stereotype.Component;

@Component
public class CustomTokenInfo implements TokenEnhancer{

    @Autowired
    private IUserService userService;

    @Override
    public OAuth2AccessToken enhance(OAuth2AccessToken accessToken, OAuth2Authentication authentication) {
        Map<String, Object> info = new HashMap<String, Object>();
        User user = userService.findByUsernanme(authentication.getName());
        info.put("name", user.getName());
        info.put("last_name", user.getLastName());
        info.put("email", user.getEmail());
        ((DefaultOAuth2AccessToken) accessToken).setAdditionalInformation(info);
        return accessToken;
    }
    
}
