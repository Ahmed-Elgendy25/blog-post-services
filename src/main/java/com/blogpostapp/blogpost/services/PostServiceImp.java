package com.blogpostapp.blogpost.services;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.blogpostapp.blogpost.dao.PostRepository;
import com.blogpostapp.blogpost.dao.UserRepository;
import com.blogpostapp.blogpost.dto.PostDTO;
import com.blogpostapp.blogpost.entity.PostEntity;
import com.blogpostapp.blogpost.entity.UserEntity;

@Service
public class PostServiceImp implements PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;

    public PostServiceImp(UserRepository userRepository, PostRepository postRepository) {
        this.userRepository = userRepository;
        this.postRepository = postRepository;
    }

    @Override 
    public Optional<PostEntity> getPostById(Integer id) {
        return postRepository.findById(id);
    }

    @Override
    public List<PostEntity> getAllPosts() {
        return postRepository.findAll();
    }

    @Transactional
    @Override
    public void deletePost(Integer id) {
        postRepository.deleteById(id);
    }

    @Override
    @Transactional
    public PostEntity uploadPost(PostEntity postRequest) {
        if (postRequest == null) {
            throw new IllegalArgumentException("Post request cannot be null");
        }

        if (postRequest.getContent() == null || postRequest.getContent().trim().isEmpty()) {
            throw new IllegalArgumentException("Post content cannot be empty");
        }

        // Get the author
        UserEntity author = userRepository.findById(postRequest.getAuthorId())
            .orElseThrow(() -> new IllegalArgumentException("Author not found with ID: " + postRequest.getAuthorId()));

        // Create new post
        PostEntity post = new PostEntity();
        post.setContent(postRequest.getContent());
        post.setAuthor(author);
        post.setDate(LocalDate.now());
        post.setDurationRead(postRequest.getDurationRead());
        post.setPostImg(postRequest.getPostImg());

        // Save and return
        return postRepository.save(post);
    }
}
