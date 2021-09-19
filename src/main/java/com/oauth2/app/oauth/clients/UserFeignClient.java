package com.oauth2.app.oauth.clients;

import com.commons.users.userscommons.models.entity.User;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "user-service")
public interface UserFeignClient {
    
    @GetMapping("/user/search/username")
    public User findByUsernanme(@RequestParam String username);

    @PutMapping("/user/{id}")
    public User update(@RequestBody User user, @PathVariable Long id);
}
