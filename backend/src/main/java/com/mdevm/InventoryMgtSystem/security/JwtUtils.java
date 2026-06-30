package com.mdevm.InventoryMgtSystem.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.function.Function;

@Service
@Slf4j
public class JwtUtils {

    // Token expiration: 6 months is too long for production. 
    // Recommended: 15-30 minutes for access tokens, use refresh tokens for longer sessions.
    // For now keeping existing value but this should be reduced in production.
    private static final long EXPIRATION_TIME_IN_MILLISEC = 1000L * 60L * 60L * 24L * 30L * 6L; //expires in 6 months in milleces
    private SecretKey key;

    @Value("${secreteJwtString}")
    private String secreteJwtString;


    @PostConstruct
    private void init() {
        byte[] keyByte = secreteJwtString.getBytes(StandardCharsets.UTF_8);
        this.key = new SecretKeySpec(keyByte, "HmacSHA256");
        
        // Warn if using default/weak secret in development
        if (secreteJwtString.contains("CHANGE_THIS") || secreteJwtString.length() < 32) {
            log.warn("WARNING: Using weak or default JWT secret! Set a strong JWT_SECRET environment variable in production.");
        }
    }

    public String generateToken(String email) {
        return Jwts.builder()
                .subject(email)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME_IN_MILLISEC))
                .signWith(key)
                .compact();
    }

    public String getUsernameFromToken(String token) {
        return extractClaims(token, Claims::getSubject);
    }

    private <T> T extractClaims(String token, Function<Claims, T> claimsTFunction) {
        return claimsTFunction.apply(Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload());
    }

    public boolean isTokeValid(String token, UserDetails userDetails) {
        final String username = getUsernameFromToken(token);
        return (username.equals(userDetails.getUsername()) && !isTokeExpired(token));
    }

    private boolean isTokeExpired(String token) {
        return extractClaims(token, Claims::getExpiration).before(new Date());
    }


}
