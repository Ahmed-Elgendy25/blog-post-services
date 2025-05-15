package com.blogpostapp.blogpost.security.models;


import java.io.Serializable; 
public class JwtResponseModel implements Serializable {
   /**
   *
   */
   private static final long serialVersionUID = 1L;
   private final String token;
   private final String type = "Bearer ";
   private final String roles;
   public JwtResponseModel(String token, String roles) {
      this.token = token;
      this.roles = roles;
   }
   public String getToken() {
      return type + token;
   }
   public String getRoles() {
      return roles;
   }
}