package com.blogpostapp.blogpost.controllers;


import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import com.blogpostapp.blogpost.dto.RegisterUserDTO;
import com.blogpostapp.blogpost.entities.UserEntity;
import com.blogpostapp.blogpost.security.JwtUserDetailsService;
import com.blogpostapp.blogpost.security.TokenManager;
import com.blogpostapp.blogpost.security.models.JwtRequestModel;
import com.blogpostapp.blogpost.security.models.JwtResponseModel;
import com.blogpostapp.blogpost.services.UserServiceImp;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;




@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private AuthenticationManager authenticationManager;

    @Autowired
    private UserServiceImp userService;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private JwtUserDetailsService userDetailsService; 
    @Autowired
    private TokenManager tokenManager;
   
    public AuthController(AuthenticationManager authenticationManager, JwtUserDetailsService userDetailsService, PasswordEncoder passwordEncoder, TokenManager tokenManager) {
        this.authenticationManager = authenticationManager;
        this.userDetailsService = userDetailsService;
        this.passwordEncoder = passwordEncoder;
        this.tokenManager = tokenManager;
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody RegisterUserDTO registeredUser) {

        if(userService.userExistByEmail(registeredUser.email())) {
            return ResponseEntity.badRequest().body("User already exists");
        }
        UserEntity newUser= new UserEntity();
        newUser.setEmail(registeredUser.email());
        newUser.setFirstName(registeredUser.firstName());
        newUser.setLastName(registeredUser.lastName());
        newUser.setUserImg(registeredUser.userImg());
        newUser.setPassword(passwordEncoder.encode(registeredUser.password()));
        
        UserEntity savedUser= userService.registerUser(newUser);
        return ResponseEntity.ok("User registered successfully: "+ savedUser);
    }

    @PostMapping("/login")
public ResponseEntity<?> createToken(@RequestBody JwtRequestModel request) {
    try {
        // Try to authenticate the user
        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );
    } catch (DisabledException e) {
        return ResponseEntity
            .status(HttpStatus.FORBIDDEN)
            .body(Map.of("error", "User account is disabled"));
    } catch (BadCredentialsException e) {
        return ResponseEntity
            .status(HttpStatus.UNAUTHORIZED)
            .body(Map.of("error", "Invalid email or password"));
    }

    // Load user details (email might not exist)
    UserDetails userDetails;
    try {
        userDetails = userDetailsService.loadUserByUsername(request.getEmail());
    } catch (UsernameNotFoundException e) {
        return ResponseEntity
            .status(HttpStatus.NOT_FOUND)
            .body(Map.of("error", "Email does not exist"));
    }

    // Generate JWT
    final String jwtToken = tokenManager.generateJwtToken(userDetails);

    // Extract roles
    List<String> roles = userDetails.getAuthorities().stream()
        .map(authority -> authority.getAuthority())
        .collect(Collectors.toList());

    // Get user ID if available
    Integer userId = null;
    if (userDetails instanceof UserEntity) {
        userId = ((UserEntity) userDetails).getId();
    }

    // Return success response
    return ResponseEntity.ok(new JwtResponseModel(jwtToken, roles, userId));
}

 
    
}





