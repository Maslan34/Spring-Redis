package com.MuharremAslan.redis.Service;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.util.Map;

@RestController
@RequestMapping("/api/otp")
public class OTPService {

    private final RedisTemplate<String, Object> redisTemplate;
    private final MailService mailService;

    public OTPService(RedisTemplate<String, Object> redisTemplate, MailService mailService) {
        this.redisTemplate = redisTemplate;
        this.mailService = mailService;
    }

    @PostMapping("/send")
    public ResponseEntity<String> sendOtp(@RequestParam String phone) {
        String otp = String.valueOf((int) ((Math.random() * 900000) + 100000));
        String key = "otp:" + phone;
        System.out.println("DEBUG >>>Key: " + key);
        redisTemplate.opsForValue().set(key, otp,Duration.ofMinutes(1)); // OTP will be kept in cache for 1 minute.
        System.out.println("DEBUG >>> Written To Redis? -> " + redisTemplate.opsForValue().get(key));


        mailService.sendOtpEmail(otp);
        System.out.println("OTP Sent: " + otp);
        return ResponseEntity.ok("OTP Send (Simulation)");
    }

    @PostMapping("/verify")
    public ResponseEntity<String> verifyOtp(@RequestBody Map<String, String> payload) {
        String phone = payload.get("phone");
        String inputOtp = payload.get("otp");
        String key = "otp:" + phone;
        System.out.println("DEBUG >>>Key: " + key);
        Object storedOtp = redisTemplate.opsForValue().get(key);
        System.out.println("DEBUG >>> storedOtp: " + storedOtp);
        if (storedOtp == null) {
            return ResponseEntity.status(400).body("OTP Expired");
        }

        if (storedOtp.toString().equals(inputOtp)) {
            redisTemplate.delete(key);
            return ResponseEntity.ok("OTP Verified!");
        } else {
            return ResponseEntity.status(400).body("Invalid OTP.");
        }
    }
}
