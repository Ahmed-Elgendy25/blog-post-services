package com.blogpostapp.blogpost.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.blogpostapp.blogpost.entity.PostEntity;

@Repository
public interface PostRepository extends JpaRepository<PostEntity, Integer> {
    
}
