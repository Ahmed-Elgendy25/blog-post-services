package com.blogpostapp.blogpost.dto;

import java.time.LocalDate;

public record ReplyResponseDTO(
    Integer id,
    String content,
    Integer authorId,
    LocalDate date,
    Integer likes,
    Integer parentReplyId
) {}