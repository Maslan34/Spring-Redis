package com.MuharremAslan.redis.Controller;

import com.MuharremAslan.redis.Service.JwtService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class JwtController {

    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public JwtController(JwtService jwtService, AuthenticationManager authenticationManager) {
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;

    }

    @GetMapping("/login")
    public String loginPage() {
        return "login"; // templates/login.html
    }
    @GetMapping("/success") // Sending jwt token to html
    public String loginSuccess(@ModelAttribute("jwtToken") String jwtToken, Model model) {
        model.addAttribute("jwtToken", jwtToken);
        return "success";
    }
    @PostMapping("/auth/login")
    public String loginUser(@RequestParam String username,
                            @RequestParam String password,
                            HttpServletResponse response, RedirectAttributes redirectAttributes) {

        try {
            Authentication auth = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(username, password)
            );

            if (auth.isAuthenticated()) {
                String jwt = jwtService.generateToken(username);

                // Sending browser via cookie
                Cookie jwtCookie = new Cookie("token", jwt);
                jwtCookie.setHttpOnly(true);
                jwtCookie.setSecure(true);
                jwtCookie.setPath("/");
                jwtCookie.setMaxAge(60 * 60); // 1 saat

                response.addCookie(jwtCookie);

                // Sending temporary data (token) to display token
                redirectAttributes.addFlashAttribute("jwtToken", jwt);
                return "redirect:/success";
            } else {
                return "redirect:/auth/login?error=true";
            }

        } catch (Exception e) {
            return "redirect:/auth/login?error=true";
        }
    }



    @PostMapping("/auth/logout")
    public String logout(@CookieValue(value = "token", required = false) String token) {

        if (token == null || token.isEmpty()) {
            throw new RuntimeException("No Token Found.");
        }

        System.out.println("Token: " + token);
        String username = jwtService.extractUsername(token);

        if (jwtService.invalidateToken(username)) {
            return "redirect:/login";
        } else {
            throw new RuntimeException("Token not deleted. User: " + username + ", Token: " + token);
        }
    }




}

