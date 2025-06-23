package com.blogpostapp.blogpost.services;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.blogpostapp.blogpost.entity.PostEntity;

public interface PostService {

    public PostEntity uploadPost(PostEntity postRequest);
    public Optional<PostEntity> getPostById(Integer id);
    public List<PostEntity> getAllPosts();
    public Page<PostEntity> getPaginatedPosts(Pageable pageable);
    public void deletePost(Integer id);
    public boolean existsByContent(String content);
}
