package com.blogpostapp.blogpost.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean 
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/login", "/api/auth/login", "/error").permitAll() // Allow both raw and prefixed
                .anyRequest().authenticated()
            )
            .httpBasic(basic -> basic.disable())   // Disable basic auth
            .formLogin(form -> form.disable());    // Disable form login
        return http.build();
    }


}

/* 
@Bean
public AuthenticationManager authenticationManager (AuthenticationConfiguration config) throws Exception {

    return config.getAuthenticationManager();
 }

 */