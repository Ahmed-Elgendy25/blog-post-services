package com.blogpostapp.blogpost.controllers;


import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.blogpostapp.blogpost.dto.AuthResponseDTO;
import com.blogpostapp.blogpost.dto.LoginUserDTO;
import com.blogpostapp.blogpost.dto.RegisterUserDTO;
import com.blogpostapp.blogpost.entity.UserEntity;
import com.blogpostapp.blogpost.security.JWTGenerator;
import com.blogpostapp.blogpost.services.UserServiceImp;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

 private AuthenticationManager authenticationManager;
 private UserServiceImp userService;
 private PasswordEncoder passwordEncoder;
 private JWTGenerator jwtGenerator;
 
 public AuthController(AuthenticationManager authenticationManager, UserServiceImp userService, PasswordEncoder passwordEncoder, JWTGenerator jwtGenerator) {
    this.authenticationManager = authenticationManager;
    this.userService = userService;
    this.passwordEncoder = passwordEncoder;
    this.jwtGenerator = jwtGenerator;
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
    newUser.setType(registeredUser.type());
    newUser.setPassword(passwordEncoder.encode(registeredUser.password()));
    
    UserEntity savedUser= userService.registerUser(newUser);
    return ResponseEntity.ok("User registered successfully: "+ savedUser);
}

@PostMapping("/login")
public ResponseEntity<AuthResponseDTO> login(@RequestBody LoginUserDTO loginUser) {
    Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginUser.email(), loginUser.password()));
    
    SecurityContextHolder.getContext().setAuthentication(authentication);
    String token = jwtGenerator.generateToken(authentication);
    return new ResponseEntity<>(new AuthResponseDTO(token), HttpStatus.OK);

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