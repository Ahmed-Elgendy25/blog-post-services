    package com.blogpostapp.blogpost.dto;

import java.time.LocalDate;

/**
 * A simplified DTO for post responses that contains only essential data
 * to minimize response size.
 */
public record PostSummaryDTO(Integer id, String title, LocalDate date, String durationRead,
    Integer authorId, String authorName,String postImg) {
    
   
}
