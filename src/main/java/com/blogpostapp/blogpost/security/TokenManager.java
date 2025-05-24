package com.blogpostapp.blogpost.security;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.Collection;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

@Component
public class TokenManager {

   private static final long serialVersionUID = 7008375124389347049L; 
   public static final long TOKEN_VALIDITY = 10 * 60 * 60; 

   @Value("${secret-key}") 
   private String jwtSecret; 

   // Generates a token on successful authentication by the user 
   // using username, issue date of token and the expiration date of the token.
   
   public String generateJwtToken(UserDetails userDetails) {
      Map<String, Object> claims = new HashMap<>();
  
      // Convert authorities to List<String>
      List<String> roles = userDetails.getAuthorities().stream()
          .map(GrantedAuthority::getAuthority) // e.g., "author"
          .collect(Collectors.toList());
  
      System.out.println("Storing roles in token: " + roles); // should be [author, user]
      claims.put("roles", roles); // ✅ NOT nested
  
      return Jwts.builder()
          .setClaims(claims)
          .setSubject(userDetails.getUsername())
          .setIssuedAt(new Date(System.currentTimeMillis()))
          .setExpiration(new Date(System.currentTimeMillis() + TOKEN_VALIDITY * 1000))
          .signWith(getKey(), SignatureAlgorithm.HS256)
          .compact();
  }
  

   // Validates the token 
   // Checks if user is an authenticatic one and using the token is the one that was generated and sent to the user. 
   // Token is parsed for the claims such as username, roles, authorities, validity period etc.
   public Boolean validateJwtToken(String token, UserDetails userDetails) { 
      final String username = getUsernameFromToken(token);
      final Claims claims = Jwts
         .parserBuilder()
         .setSigningKey(getKey())
         .build()
         .parseClaimsJws(token).getBody(); 
      Boolean isTokenExpired = claims.getExpiration().before(new Date());
      return (username.equals(userDetails.getUsername())) && !isTokenExpired;
   }

   public Claims getAllClaimsFromToken(String token) {
      return Jwts.parserBuilder()
              .setSigningKey(getKey())
              .build()
              .parseClaimsJws(token)
              .getBody();
  }
  
   // get the username by checking subject of JWT Token
   public String getUsernameFromToken(String token) {
      final Claims claims = Jwts
         .parserBuilder()
         .setSigningKey(getKey())
         .build()
         .parseClaimsJws(token).getBody(); 
      return claims.getSubject(); 
   }

   public String getRoleFromToken(String token) {
      final Claims claims = Jwts
         .parserBuilder()
         .setSigningKey(getKey())
         .build()
         .parseClaimsJws(token).getBody(); 
      return claims.get("roles").toString();
   }
   // create a signing key based on secret
   private Key getKey() {
      byte[] keyBytes = Decoders.BASE64.decode(jwtSecret);		
      Key key = Keys.hmacShaKeyFor(keyBytes);
      return key;
   }
}