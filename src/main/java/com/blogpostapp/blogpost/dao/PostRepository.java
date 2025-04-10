package com.blogpostapp.blogpost.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.blogpostapp.blogpost.entity.Post;

@Repository
public interface PostRepository extends JpaRepository<Post, Integer> {
    
}
