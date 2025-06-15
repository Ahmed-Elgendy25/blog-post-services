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
   private final Integer userId;
   
   public JwtResponseModel(String token, List<String> roles, Integer userId) {
      this.token = token;
      this.roles = roles;
      this.userId = userId;
   }
   
   public String getToken() {
      return type + token;
   }
   
   public List<String> getRoles() {
      return roles;
   }
   
   public Integer getUserId() {
      return userId;
   }
}