package com.blogpostapp.blogpost.dto;
import java.time.LocalDate;

public record PostDTO( String content, Integer authorId, String durationRead, String postImg, String title, String subTitle, LocalDate date) {
    
}
