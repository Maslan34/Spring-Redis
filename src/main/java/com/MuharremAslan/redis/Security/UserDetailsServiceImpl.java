package com.MuharremAslan.redis.Security;

import com.MuharremAslan.redis.Model.User;
import com.MuharremAslan.redis.Service.UserServiceSecurity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserServiceSecurity userService;

    @Autowired
    @Lazy
    public UserDetailsServiceImpl(UserServiceSecurity userService) {
        this.userService = userService;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        Optional<User> user  = userService.findByUsername(username);
        return user.orElseThrow(() -> new UsernameNotFoundException("Redis App:"+username+" not found!" ));

    }



}