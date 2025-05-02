package com.blogpostapp.blogpost.security.models;


import java.io.Serializable; 
public class JwtResponseModel implements Serializable {
   /**
   *
   */
   private static final long serialVersionUID = 1L;
   private final String token;
   private final String type = "Bearer ";
   public JwtResponseModel(String token) {
      this.token = token;
   }
   public String getToken() {
      return type + token;
   }
}