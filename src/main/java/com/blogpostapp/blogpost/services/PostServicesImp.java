package com.blogpostapp.blogpost.services;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.blogpostapp.blogpost.dao.PostRepository;
import com.blogpostapp.blogpost.dao.UserRepository;
import com.blogpostapp.blogpost.entity.Post;
import com.blogpostapp.blogpost.entity.User;

import jakarta.transaction.Transactional;

@Service
public class PostServicesImp implements PostServices {


    private PostRepository postRepository;
    private UserRepository userRepository;
   public PostServicesImp(UserRepository theUserRepository,PostRepository thePostRepository) {
        userRepository=theUserRepository;
        postRepository=thePostRepository;
    }

    @Override
    public Post uploadPost(Post post, Integer authorId) {
        // Fetch the author from database
        User author = userRepository.findById(authorId)
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
    public Optional<Post> getPostById(Integer id) {
        Optional<Post> singlePost=postRepository.findById(id);
        return singlePost;
    }


    @Override
    public List<Post> getAllPosts() {
        List<Post> allPosts = postRepository.findAll();
        return allPosts;
    }

    @Transactional
    @Override
    public void deletePost(Integer id) {
      // TODO later
    }


}
