

package com.blogpostapp.blogpost.security;

import java.util.ArrayList;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User; 
import org.springframework.security.core.userdetails.UserDetails; 
import org.springframework.security.core.userdetails.UserDetailsService; 
import org.springframework.security.core.userdetails.UsernameNotFoundException; 
import org.springframework.stereotype.Service;

import com.blogpostapp.blogpost.dao.UserRepository;
import com.blogpostapp.blogpost.entities.UserEntity; 

@Service
public class JwtUserDetailsService implements UserDetailsService { 
    
    private UserRepository userRepository;
    
    @Autowired
    public JwtUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
@Override 
public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
   Optional<UserEntity> user = userRepository.findByEmail(username);
   if (user.isPresent()) { 
      // Return the actual UserEntity instead of creating a new User
      return user.get();
   } else { 
      throw new UsernameNotFoundException("User not found with username: " + username); 
   } 
}
}