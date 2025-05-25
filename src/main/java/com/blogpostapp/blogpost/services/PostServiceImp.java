package com.blogpostapp.blogpost.services;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.blogpostapp.blogpost.dao.PostRepository;
import com.blogpostapp.blogpost.dao.UserRepository;
import com.blogpostapp.blogpost.entity.PostEntity;
import com.blogpostapp.blogpost.entity.UserEntity;

@Service
public class PostServiceImp implements PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;

    @Autowired
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

    @Override
    public Page<PostEntity> getPaginatedPosts(Pageable pageable) {
        return postRepository.findAll(pageable);
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

        // Save the post first to generate an ID "increment id"
        PostEntity savedPost = postRepository.save(post);
        
        // Add the author to the collaborators list
        author.getCollaboratedPosts().add(savedPost);



        // Save the user to generate an ID "increment id"

        userRepository.save(author);

        return savedPost;
    }
}
