package com.blogpostapp.blogpost.dao;

import com.blogpostapp.blogpost.entity.CommentEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<CommentEntity, Integer> {
    
    @Query("FROM CommentEntity c WHERE c.post.id = :postId ORDER BY c.createdAt DESC")
    Page<CommentEntity> findByPostIdOrderByCreatedAtDesc(@Param("postId") Integer postId, Pageable pageable);
    
    @Query("FROM CommentEntity c WHERE c.post.id = :postId AND c.parentComment IS NULL ORDER BY c.createdAt DESC")
    Page<CommentEntity> findTopLevelCommentsByPostId(@Param("postId") Integer postId, Pageable pageable);
    
    @Query("FROM CommentEntity c WHERE c.parentComment.id = :parentCommentId ORDER BY c.createdAt ASC")
    List<CommentEntity> findRepliesByParentCommentId(@Param("parentCommentId") Integer parentCommentId);
    
    @Query("SELECT COUNT(c) FROM CommentEntity c WHERE c.post.id = :postId")
    Long countByPostId(@Param("postId") Integer postId);
    
    @Query("FROM CommentEntity c WHERE c.author.id = :authorId ORDER BY c.createdAt DESC")
    Page<CommentEntity> findByAuthorIdOrderByCreatedAtDesc(@Param("authorId") Integer authorId, Pageable pageable);
}