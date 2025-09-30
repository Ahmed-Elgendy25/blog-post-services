package com.blogpostapp.blogpost.dto;

import java.time.LocalDate;

public record PostHateoasDTO(
    Integer id,
    String content,
    Integer authorId,
    String durationRead,
    String postImg,
    String title,
    String subTitle,
    LocalDate date,
    Integer likes
) {}