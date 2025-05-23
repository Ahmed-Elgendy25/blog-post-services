package com.blogpostapp.blogpost.security;

import java.io.IOException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import io.jsonwebtoken.ExpiredJwtException;

@Component
public class JwtFilter extends OncePerRequestFilter {
   @Autowired
   private JwtUserDetailsService userDetailsService;
   @Autowired
   private TokenManager tokenManager;
   @Override
   protected void doFilterInternal(HttpServletRequest request,
      HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {

      String tokenHeader = request.getHeader("Authorization");
      String username = null;
      String token = null;
      String roles = null;
      // Check if Authorization header exists
      if (tokenHeader != null) {
         // If header starts with Bearer, extract token after it
         if (tokenHeader.startsWith("Bearer ")) {
            token = tokenHeader.substring(7);
         } else {
            // Otherwise, use the entire header value as the token
            token = tokenHeader;
         }
         
         try {
            username = tokenManager.getUsernameFromToken(token);
            roles = tokenManager.getRoleFromToken(token);
         } catch (IllegalArgumentException e) {
            System.out.println("Unable to get JWT Token");
         } catch (ExpiredJwtException e) {
            System.out.println("JWT Token has expired");
         } catch (Exception e) {
            System.out.println("Invalid JWT Token: " + e.getMessage());
         }
      } else {
         System.out.println("Authorization header not found");
      }
      // validate the JWT Token and create a new authentication token and set in security context	  
      if (null != username && SecurityContextHolder.getContext().getAuthentication() == null) {
         UserDetails userDetails = userDetailsService.loadUserByUsername(username);
         if (tokenManager.validateJwtToken(token, userDetails)) {
            UsernamePasswordAuthenticationToken
               authenticationToken = new UsernamePasswordAuthenticationToken(
                  userDetails, null, userDetails.getAuthorities());
               authenticationToken.setDetails(new
                  WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authenticationToken);
         }
      }
      filterChain.doFilter(request, response);
   }
}