package com.blogpostapp.blogpost.services;

import com.blogpostapp.blogpost.entity.CommentEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface CommentService {
    CommentEntity createComment(CommentEntity comment);
    Optional<CommentEntity> getCommentById(Integer id);
    Page<CommentEntity> getCommentsByPostId(Integer postId, Pageable pageable);
    Page<CommentEntity> getTopLevelCommentsByPostId(Integer postId, Pageable pageable);
    List<CommentEntity> getRepliesByParentCommentId(Integer parentCommentId);
    CommentEntity updateComment(Integer id, String content);
    void deleteComment(Integer id);
    CommentEntity likeComment(Integer id);
    Long countCommentsByPostId(Integer postId);
    Page<CommentEntity> getCommentsByAuthorId(Integer authorId, Pageable pageable);
}