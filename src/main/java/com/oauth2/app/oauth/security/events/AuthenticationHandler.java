package com.oauth2.app.oauth.security.events;

import com.commons.users.userscommons.models.entity.User;
import com.oauth2.app.oauth.services.IUserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationEventPublisher;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.stereotype.Component;

import brave.Tracer;
import feign.FeignException;

@Component
public class AuthenticationHandler implements AuthenticationEventPublisher {

    @Autowired
    private IUserService userService;

    @Autowired
    private Tracer tracer;

    @Override
    public void publishAuthenticationSuccess(Authentication authentication) {

        

        //if(authentication.getName().equalsIgnoreCase("front-end-app")){
        if (authentication.getDetails() instanceof WebAuthenticationDetails) {
            return;
        }
        UserDetails user = (UserDetails) authentication.getPrincipal();
        System.out.println("Success Login: " + user.getUsername());

        User userSer = userService.findByUsernanme(authentication.getName());
        if(userSer.getAttempts() != null && userSer.getAttempts() > 0){
            userSer.setAttempts(0);
            userService.update(userSer, userSer.getId());
        }
    }

    @Override
    public void publishAuthenticationFailure(AuthenticationException exception, Authentication authentication) {
        System.out.println("Fail Login with error: " + exception.getMessage());
        
        try {
            StringBuilder errors = new StringBuilder();
            User user = userService.findByUsernanme(authentication.getName());
            if (user.getAttempts() == null) {
                user.setAttempts(0);
            }
            user.setAttempts(user.getAttempts()+1);
            errors.append(" - " + "Attemps: " + user.getAttempts());
            errors.append("Fail Login with error: " + exception.getMessage());
            if(user.getAttempts() >= 3){
                System.out.println(String.format("User %s disabled for 3 attempts or more", authentication.getName()));
                errors.append(" - " + String.format("User %s disabled for 3 attempts or more", authentication.getName()));
                user.setEnabled(false);
            }
            tracer.currentSpan().tag("Message", errors.toString());
            userService.update(user, user.getId());
        } catch (FeignException e) {
            System.out.println(String.format("User %s don't exits", authentication.getName()));
            tracer.currentSpan().tag("Message", String.format("User %s don't exits", authentication.getName()));
        }
        

    }

}
