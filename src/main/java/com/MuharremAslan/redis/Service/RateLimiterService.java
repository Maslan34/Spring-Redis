package com.MuharremAslan.redis.Service;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
public class RateLimiterService {

    private final RedisTemplate<String, Integer> redisTemplate;
    private final RedisTemplate<String, String> blacklistTemplate;

    public RateLimiterService(RedisTemplate<String, Integer> redisTemplate, @Qualifier("redisTemplateBlacklist") RedisTemplate<String, String> blacklistTemplate) {
        this.redisTemplate = redisTemplate;
        this.blacklistTemplate = blacklistTemplate;
    }

    //We could also write this function as thread-safe.
    // To prevent more than one thread from processing at the same time,
    // however, this could cause performance issues for each request.
    // By adding the >= condition, the process will be blocked for other active threads when this threshold value is exceeded.

    public boolean isAllowed(String key) {

        Integer keyInRedis = redisTemplate.opsForValue().get(key);

        // Checking Ip is in blacklist
        Boolean exists = blacklistTemplate.hasKey("blacklist:" + key);
        if (exists)
            return false;


        // Creating new counter for that IP
        if (keyInRedis == null) {
            redisTemplate.opsForValue().set(key, 0, Duration.ofMinutes(10));
            return true;
        } else if (keyInRedis >= 10) { // Reached to rate and adding that IP to blacklist
            String keyOutRedis = "blacklist:" + key;
            blacklistTemplate.opsForValue().set(keyOutRedis, "blocked", Duration.ofMinutes(10));
            redisTemplate.delete(key);
            return false;
        } else {
            redisTemplate.opsForValue().increment(key, 1); // We use inc method here to ensure thread-safety
            // because we want only one thread to do this incrementing operation at a time
            return true;
        }


    }
}
