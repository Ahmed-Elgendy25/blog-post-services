package com.blogpostapp.blogpost.services;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.blogpostapp.blogpost.entities.PostEntity;

public interface PostService {
    PostEntity uploadPost(PostEntity postRequest);
    Optional<PostEntity> getPostById(Integer id);
    List<PostEntity> getAllPosts();
    Page<PostEntity> getPaginatedPosts(Pageable pageable);
    void deletePost(Integer id);
    boolean existsByContent(String content);
}
