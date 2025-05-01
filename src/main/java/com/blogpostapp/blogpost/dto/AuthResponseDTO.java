package com.blogpostapp.blogpost.dto;

public class AuthResponseDTO {
    private String accessToken;
    private String tokenType = "Bearer ";

    public AuthResponseDTO(String theAccessToken) {  
    this.accessToken =  theAccessToken;
    }
}
