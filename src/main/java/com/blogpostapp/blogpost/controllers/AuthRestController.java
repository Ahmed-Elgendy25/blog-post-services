package com.blogpostapp.blogpost.controllers;


import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.blogpostapp.blogpost.dto.RegisterUserDTO;
import com.blogpostapp.blogpost.entity.UserEntity;
import com.blogpostapp.blogpost.services.UserService;

@RestController
@RequestMapping("/api/auth")
public class AuthRestController {

 private AuthenticationManager authenticationManager;
 private UserService userService;
 private PasswordEncoder passwordEncoder;
    
 @Autowired
 public AuthRestController(AuthenticationManager authenticationManager, UserService userService, PasswordEncoder passwordEncoder) {
    this.authenticationManager = authenticationManager;
    this.userService = userService;
    this.passwordEncoder = passwordEncoder;
}

@PostMapping("/register")
public ResponseEntity<String> register(@RequestBody RegisterUserDTO registerUserDTO) {

    if(userService.userExistByEmail(registerUserDTO.email())) {
        return ResponseEntity.badRequest().body("User already exists");
    }
    UserEntity newUser= new UserEntity();
    newUser.setEmail(registerUserDTO.email());
    newUser.setFirstName(registerUserDTO.firstName());
    newUser.setLastName(registerUserDTO.lastName());
    newUser.setUserImg(registerUserDTO.userImg());
    newUser.setType(registerUserDTO.type());
    newUser.setPassword(passwordEncoder.encode(registerUserDTO.password()));
    
    UserEntity savedUser= userService.registerUser(newUser);
    return ResponseEntity.ok("User registered successfully: "+ savedUser);
}
}

/*
 *     private JwtService jwtService;
    // private AuthenticationManager authManager;
    private UserService userService;

    public AuthRestController(JwtService jwtService, UserService theUserService) {
        this.jwtService = jwtService;
        this.userService = theUserService;
    }
@PostMapping("/login")
public String  login(@RequestBody AuthUserDTO user) {
    if (userService.loginUser(user) == false) {
        throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials");
    }
    return jwtService.generateToken(user.email());
}

 */



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