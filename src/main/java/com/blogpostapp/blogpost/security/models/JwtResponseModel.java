package com.blogpostapp.blogpost.security.models;

import java.io.Serializable;
import java.util.List; 

public class JwtResponseModel implements Serializable {
   /**
   *
   */
   private static final long serialVersionUID = 1L;
   private final String token;
   private final String type = "Bearer ";
   private final List<String> roles;
   
   public JwtResponseModel(String token, List<String> roles) {
      this.token = token;
      this.roles = roles;
   }
   
   public String getToken() {
      return type + token;
   }
   
   public List<String> getRoles() {
      return roles;
   }
}