package com.blogpostapp.blogpost.services;

import java.security.Key;
import java.util.Date;
import org.springframework.stereotype.Component;
import io.jsonwebtoken.Jwts;

@Component
public class JwtService {


    public String generateToken(String email) {
        return createToken(email);
    }
        
    private String createToken( String email) {
        Key key = Jwts.SIG.HS256.key().build();

        return Jwts.builder()
            .claim("sub", email) // Subject (email)
            .claim("iat", new Date(System.currentTimeMillis())) // Issued at timestamp
            .claim("exp", new Date(System.currentTimeMillis() + 1000 * 60 * 30)) // Expiry in 30 minutes
            .signWith(key) // Sign with your secret key
            .compact(); // Finalize and return the token
    }
    
    
  
}
    
