package com.blogpostapp.blogpost.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import org.springframework.stereotype.Repository;

import com.blogpostapp.blogpost.entity.PostEntity;

import java.util.Optional;

@Repository
public interface PostRepository extends JpaRepository<PostEntity, Integer> {
    
    @Query("SELECT COUNT(p) > 0 FROM PostEntity p WHERE p.content = :content")
    boolean existsByContent(@Param("content") String content);
    
    @Query("FROM PostEntity WHERE content = :content")
    Optional<PostEntity> findByContent(@Param("content") String content);
}
