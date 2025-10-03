package com.blogpostapp.blogpost.security;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;

import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;



@Configuration 
@EnableWebSecurity
public class WebSecurityConfig {

   @Autowired
   private JwtAuthenticationEntryPoint authenticationEntryPoint;
   @Autowired
   private JwtFilter filter;



   @Bean
   protected SecurityFilterChain filterChain(HttpSecurity http) throws Exception { 
      return http
         .csrf(AbstractHttpConfigurer::disable)
         .authorizeHttpRequests(
            request -> request.requestMatchers("/api/auth/**").permitAll()
                  .requestMatchers("/api/posts/**").permitAll()  // Allow public access to all posts endpoints
                  .requestMatchers("/api/users/**").permitAll()     // Allow public access to user info
                  .requestMatchers("/api/posts/create-article").hasAuthority("author")  // Restrict creation to authors
                  .anyRequest().authenticated()
            
         )
         // Send a 401 error response if user is not authentic.		 
         .exceptionHandling(exception -> exception.authenticationEntryPoint(authenticationEntryPoint))
         // no session management
         .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) 
         // filter the request and add authentication token		 
         .addFilterBefore(filter,  UsernamePasswordAuthenticationFilter.class)
         .build();
   }


   @Bean 
   protected PasswordEncoder passwordEncoder() { 
      return new BCryptPasswordEncoder(); 
   }
      @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}