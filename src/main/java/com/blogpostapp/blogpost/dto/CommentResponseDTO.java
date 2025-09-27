package com.blogpostapp.blogpost.dto;

import java.time.LocalDateTime;

public record CommentResponseDTO(
    Integer id,
    String content,
    LocalDateTime createdAt,
    LocalDateTime updatedAt,
    Integer postId,
    Integer authorId,
    String authorName,
    Integer parentCommentId,
    Integer likes
) {}