
package com.MuharremAslan.redis.Service;

import com.MuharremAslan.redis.Model.User;
import com.MuharremAslan.redis.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserServiceSecurity {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    public UserServiceSecurity(UserRepository userRepository, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.userRepository = userRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    public Optional<User> findByUsername(String username) {
        Optional<User> user = userRepository.findByUsername(username);
        return userRepository.findByUsername(username);
    }

    public User createUser(User request) {
        request.setPassword(bCryptPasswordEncoder.encode(request.getPassword()));
        return userRepository.save(request);

    }

}
