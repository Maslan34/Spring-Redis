package com.MuharremAslan.redis.Controller;


import com.MuharremAslan.redis.Model.User;
import com.MuharremAslan.redis.Service.JwtService;
import com.MuharremAslan.redis.Service.UserServiceSecurity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@Slf4j
public class AuthController {

    private final UserServiceSecurity userService;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;


    public AuthController(UserServiceSecurity userService, JwtService jwtService, AuthenticationManager authenticationManager) {
        this.userService = userService;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;

    }


    @PostMapping("/addNewUser")
    public User addNewUser(@RequestBody User request) {
        return userService.createUser(request);

    }


    //Redis - Security - JWT
    @PostMapping("/users")
    public ResponseEntity<User> createUser(@RequestBody User user) {
        return ResponseEntity.ok(userService.createUser(user));
    }

    @GetMapping("/test")
    public ResponseEntity<String> testEndpoint() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return ResponseEntity.ok("Hi " + auth.getName() + "! Test Success :).");
    }


    @PostMapping("/generateToken")
    public String generateToken(@RequestBody User request) {
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));
        if (authentication.isAuthenticated()) {
            String token = jwtService.generateToken(request.getUsername());

            System.out.println("token: " + token);
            return token;
        }
        log.info("username or password not valid" + request.getUsername());
        throw new UsernameNotFoundException("invalid username or password" + request.getUsername());

    }


}