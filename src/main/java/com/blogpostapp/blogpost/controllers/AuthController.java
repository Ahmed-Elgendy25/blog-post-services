package com.blogpostapp.blogpost.controllers;


import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.blogpostapp.blogpost.dto.LoginUserDTO;
import com.blogpostapp.blogpost.dto.RegisterUserDTO;
import com.blogpostapp.blogpost.entity.UserEntity;
import com.blogpostapp.blogpost.security.JwtUserDetailsService;
import com.blogpostapp.blogpost.security.TokenManager;
import com.blogpostapp.blogpost.security.models.JwtRequestModel;
import com.blogpostapp.blogpost.security.models.JwtResponseModel;
import com.blogpostapp.blogpost.services.UserServiceImp;

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
    newUser.setType(registeredUser.type());
    newUser.setPassword(passwordEncoder.encode(registeredUser.password()));
    
    UserEntity savedUser= userService.registerUser(newUser);
    return ResponseEntity.ok("User registered successfully: "+ savedUser);
}

 @PostMapping("/login")
   public ResponseEntity<JwtResponseModel> createToken(@RequestBody JwtRequestModel
      request) throws Exception {
      try {
         authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
      } catch (DisabledException e) {
         throw new Exception("USER_DISABLED", e);
      } catch (BadCredentialsException e) {
         throw new Exception("INVALID_CREDENTIALS", e);
      }
      final UserDetails userDetails = userDetailsService.loadUserByUsername(request.getEmail());
      final String jwtToken = tokenManager.generateJwtToken(userDetails);
      
      // Collect all roles from authorities
      List<String> roles = userDetails.getAuthorities().stream()
          .map(authority -> authority.getAuthority())
          .collect(Collectors.toList());
      
      return ResponseEntity.ok(new JwtResponseModel(jwtToken, roles));
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