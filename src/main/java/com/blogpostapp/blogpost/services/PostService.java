package com.blogpostapp.blogpost.services;

import java.util.List;
import java.util.Optional;

import com.blogpostapp.blogpost.entity.PostEntity;

public interface PostService {

    public PostEntity uploadPost(PostEntity postRequest);
    public Optional<PostEntity> getPostById(Integer id);
    public List<PostEntity> getAllPosts();
    public void deletePost(Integer id);
}
