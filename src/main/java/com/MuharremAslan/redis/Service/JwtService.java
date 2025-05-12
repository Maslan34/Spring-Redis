package com.MuharremAslan.redis.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


@Service
public class JwtService {

    @Value("${jwt.key}")
    private String SECRET_KEY;

    public String generateToken(String username) {
        Map<String, Object> payloads = new HashMap<String, Object>();
        payloads.put("muharrem", "aslan");
        return createToken(payloads, username);

    }

    public boolean validateToken(String token, UserDetails userDetails) {
        try {
            final String username = extractUserDetails(token);
            Claims claims = extractClaims(token);
            return (username.equals(userDetails.getUsername()) && !isTokenExpired(claims));
        } catch (Exception e) {
            return false;
        }
    }

    public String extractUserDetails(String token) {
        Claims claims = Jwts
                .parser()
                .verifyWith(getSignKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();

        return claims.getSubject();
    }


    private Claims extractClaims(String token) {
        // Extract claims after signature verification
        return Jwts
                .parser()
                .verifyWith(getSignKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private boolean isTokenExpired(Claims claims) {
        return claims.getExpiration().before(new Date());  // Token'ın bitiş tarihi geçmiş mi?
    }


    private String createToken(Map<String, Object> payloads, String username) {
        return Jwts.builder()
                .setClaims(payloads)
                .setSubject(username)
                .setIssuedAt(new Date())
                //.setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 2))  // Timeout değeri -> 30 dk mili saniye cinsinden
                .signWith(getSignKey(), SignatureAlgorithm.HS256).compact();
    }

    private SecretKey getSignKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }


}