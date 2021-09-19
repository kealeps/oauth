package com.oauth2.app.oauth.services;

import java.util.List;
import java.util.stream.Collectors;

import com.commons.users.userscommons.models.entity.User;
import com.oauth2.app.oauth.clients.UserFeignClient;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import brave.Tracer;
import feign.FeignException;

@Service
public class UserService implements IUserService, UserDetailsService {

    @Autowired
    private Tracer tracer;

    @Autowired
    private UserFeignClient client;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        try {
            User user = client.findByUsernanme(username);
            List<GrantedAuthority> authorities = user.getRoles().stream()
                    .map(role -> new SimpleGrantedAuthority(role.getName())).collect(Collectors.toList());
            return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(),
                    user.getEnabled(), true, true, true, authorities);
        } catch (FeignException e) {
            String error = "Usuario '" + username + "' no encontrado";
            tracer.currentSpan().tag("Message", error + ": " + e.getMessage());
            throw new UsernameNotFoundException(error);
        }
    }

    @Override
    public User findByUsernanme(String username) {
        return client.findByUsernanme(username);
    }

    @Override
    public User update(User user, Long id) {
        return client.update(user, id);
    }

}
