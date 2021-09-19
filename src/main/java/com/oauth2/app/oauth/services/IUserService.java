package com.oauth2.app.oauth.services;

import com.commons.users.userscommons.models.entity.User;

public interface IUserService {

    public User findByUsernanme(String username);

    public User update(User user, Long id);
    
}
