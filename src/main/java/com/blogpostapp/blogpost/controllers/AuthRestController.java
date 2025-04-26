package com.blogpostapp.blogpost.controllers;


import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import com.blogpostapp.blogpost.dto.AuthUserDTO;
import com.blogpostapp.blogpost.services.JwtService;
import com.blogpostapp.blogpost.services.UserService;

@RestController
@RequestMapping("/api/auth")
public class AuthRestController {

    private JwtService jwtService;
    // private AuthenticationManager authManager;
    private UserService userService;

    public AuthRestController(JwtService jwtService, UserService theUserService) {
        this.jwtService = jwtService;
        this.userService = theUserService;
    }
@PostMapping("/login")
public String authAndGetToken(@RequestBody AuthUserDTO user) {
    if (userService.loginUser(user) == false) {
        throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials");
    }
    return jwtService.generateToken(user.email());
}


}


/*
    @PostMapping("/login")
public ResponseEntity<String> authAndGetToken(@RequestBody AuthUserDTO user) {
    try {
        Authentication authentication = authManager.authenticate(
            new UsernamePasswordAuthenticationToken(user.email(), user.password())
        );
        if (authentication.isAuthenticated()) {
            String token = jwtService.generateToken(user.email());
            return ResponseEntity.ok(token);
        }
        throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authentication failed");
    } catch (Exception e) {
        throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials", e);
    }
}
     */