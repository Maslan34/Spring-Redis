package com.MuharremAslan.redis.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class OTPController {

    @GetMapping("/api/otp")
    public String serveReactApp() {
        return "forward:/index.html";
    }
}
