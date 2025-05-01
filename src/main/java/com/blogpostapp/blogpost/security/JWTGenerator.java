package com.blogpostapp.blogpost.security;

import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Component
public class JWTGenerator {

    public String generateToken(Authentication authentication) {
        String email = authentication.getName();
        Date currentDate = new Date();
        Date expiryDate = new Date(currentDate.getTime() + SecurityConstants.JWT_EXPIRATION);

        String token = Jwts.builder()
                .subject(email)
                .issuedAt(new Date())
                .expiration(expiryDate)
                .signWith(getSignInKey())
                .compact();
        return token;
    }
 private SecretKey getSignInKey() {
  byte[] keyBytes = SecurityConstants.JWT_SECRET.getBytes();
        return Keys.hmacShaKeyFor(keyBytes);
    }


public String getUserNameFromToken(String token) {
    return Jwts.parser()
            .verifyWith(getSignInKey())
            .build()
            .parseSignedClaims(token)
            .getPayload()
            .getSubject();
          
               
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                   .verifyWith(getSignInKey())
                   .build()
                   .parseSignedClaims(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
