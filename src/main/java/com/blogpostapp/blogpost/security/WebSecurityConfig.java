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
            request -> request.requestMatchers("/api/auth/**","/api/posts/{id}/hateoas").permitAll()
                  .requestMatchers("/api/posts/create-article","api/posts/paginated").hasAuthority("author")
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