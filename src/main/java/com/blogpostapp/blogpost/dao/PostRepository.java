package com.blogpostapp.blogpost.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import org.springframework.stereotype.Repository;

import com.blogpostapp.blogpost.entity.PostEntity;

import java.util.Optional;

@Repository
public interface PostRepository extends JpaRepository<PostEntity, Integer> {
    
    @Query("FROM PostEntity WHERE content = :content")
    Optional<PostEntity> findByContent(@Param("content") String content);
}
