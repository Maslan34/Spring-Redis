package com.MuharremAslan.redis.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.time.Duration;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class JwtService {

    private static final String REDIS_PREFIX = "jwt:";
    private static final Duration TOKEN_EXPIRATION = Duration.ofMinutes(10);

    @Value("${jwt.key}")
    private String SECRET_KEY;
    private final RedisTemplate<String, String> redisTemplate;

    public JwtService(@Qualifier("redisTemplate") RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public String generateToken(String username) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("Muharrem", "Aslan"); //Custom claim

        //expiration date was not used because we did it through redis
        String token = Jwts.builder()
                .setClaims(claims)
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + TOKEN_EXPIRATION.toMillis()))
                .signWith(getSignKey(), SignatureAlgorithm.HS256)
                .compact();

        redisTemplate.opsForValue().set(
                REDIS_PREFIX + username,
                token,
                TOKEN_EXPIRATION
        );
        return token;
    }

    public boolean invalidateToken(String username) {
        Boolean deleted = redisTemplate.delete(REDIS_PREFIX + username);
        return deleted != null && deleted;
    }

    public boolean validateToken(String token, UserDetails userDetails) {
        try {
            final String username = extractUsername(token);

           // Checking Jwt structure valid
            Claims claims = extractAllClaims(token);

            // Checking jwt in redis
            String redisToken = redisTemplate.opsForValue().get(REDIS_PREFIX + username);
            if (redisToken == null || !redisToken.equals(token)) {
                return false;
            }

            // Basic security process
            if (userDetails != null && !username.equals(userDetails.getUsername())) {
                return false;
            }

            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public String extractUsername(String token) {
        return extractAllClaims(token).getSubject();
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSignKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private SecretKey getSignKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}