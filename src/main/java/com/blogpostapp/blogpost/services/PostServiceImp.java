package com.blogpostapp.blogpost.services;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.blogpostapp.blogpost.dao.PostRepository;
import com.blogpostapp.blogpost.dao.UserRepository;
import com.blogpostapp.blogpost.entity.PostEntity;
import com.blogpostapp.blogpost.entity.UserEntity;

import jakarta.transaction.Transactional;

@Service
public class PostServiceImp implements PostService {


    private PostRepository postRepository;
    private UserRepository userRepository;
   public PostServiceImp(UserRepository theUserRepository,PostRepository thePostRepository) {
        userRepository=theUserRepository;
        postRepository=thePostRepository;
    }

    @Override
    public PostEntity uploadPost(PostEntity post, Integer authorId) {
        // Fetch the author from database
        UserEntity author = userRepository.findById(authorId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + authorId));
        
        // Set the author
        post.setAuthor(author);
        
        // Set current date if not provided
        if(post.getDate() == null) {
            post.setDate(LocalDate.now());
        }
        
        // Save the post
        return postRepository.save(post);
    }

@Override 
    public Optional<PostEntity> getPostById(Integer id) {
        Optional<PostEntity> singlePost=postRepository.findById(id);
        return singlePost;
    }


    @Override
    public List<PostEntity> getAllPosts() {
        List<PostEntity> allPosts = postRepository.findAll();
        return allPosts;
    }

    @Transactional
    @Override
    public void deletePost(Integer id) {
      // TODO later
    }


}
