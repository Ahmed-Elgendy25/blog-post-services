package com.blogpostapp.blogpost.services;

import java.util.List;
import java.util.Optional;

import com.blogpostapp.blogpost.entity.Post;

public interface PostServices {

    public Post uploadPost(Post post,Integer authorId);
    public Optional<Post> getPostById(Integer id);
    public List<Post> getAllPosts();
    public void deletePost(Integer id);
}
