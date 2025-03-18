package com.example.movietickets.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtUtil {
    private static final String SECRET_KEY = "01234567890123456789012345678901";
    private static final long EXPIRATION_TIME = 86400000; // 1 day
    private static final Logger logger = LoggerFactory.getLogger(JwtUtil.class);


    private final Key key = Keys.hmacShaKeyFor(SECRET_KEY.getBytes());

    public String generateToken(String email) {
        return Jwts.builder()
                .setSubject(email)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public String extractEmail(String token) {
    	try {
            String email = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody()
                    .getSubject();
            logger.debug("Extracted email from token: {}", email);
            return email;
        } catch (Exception e) {
            logger.error("Failed to extract email from token: {}", e.getMessage());
            return null;
        }
    }

    public boolean validateToken(String token) {
        try {
        	logger.debug("Received Token: [{}]", token);
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            logger.debug("Token is valid!");
            return true;
        } catch (Exception e) {
        	logger.error("Invalid Token: {}", e.getMessage());
            return false;
        }
    }
}
