package com.blogpostapp.blogpost.dto;

public record CommentRequestDTO(
    String content,
    Integer postId,
    Integer authorId,
    Integer parentCommentId
) {}