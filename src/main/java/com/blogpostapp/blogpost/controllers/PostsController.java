package com.blogpostapp.blogpost.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.blogpostapp.blogpost.dto.PostDTO;
import com.blogpostapp.blogpost.entity.PostEntity;
import com.blogpostapp.blogpost.entity.UserEntity;
import com.blogpostapp.blogpost.services.PostServiceImp;


import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@RestController
@RequestMapping("/api/posts")
public class PostsController {

    private final PostServiceImp postServices;

    @Autowired
    public PostsController(PostServiceImp postService) {
        this.postServices = postService;
    }

    @PostMapping("/create-article")
    public ResponseEntity<?> uploadPost(@RequestBody PostDTO post) {
        try {
            if (post == null || post.authorId() == null) {
                return ResponseEntity.badRequest().body("Post and author ID are required");
            }

            // Create post entity from DTO
            PostEntity postEntity = new PostEntity();
            postEntity.setContent(post.content());
            
            // Create a temporary author reference
            UserEntity author = new UserEntity();
            author.setId(post.authorId());
            postEntity.setAuthor(author);
            
            // Set optional fields
            if (post.durationRead() != null) {
                postEntity.setDurationRead(post.durationRead());
            }
            if (post.postImg() != null) {
                postEntity.setPostImg(post.postImg());
            }

            // Save the post
            PostEntity savedPost = postServices.uploadPost(postEntity);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedPost);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                               .body("Error creating post: " + e.getMessage());
        }
    }

    @GetMapping("/get-article/{id}")
    public ResponseEntity<?> getPostById(@PathVariable Integer id) {
        try {
            Optional<PostEntity> post = postServices.getPostById(id);
            return post.map(p -> ResponseEntity.ok().body(p))
                      .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                               .body("Error retrieving post: " + e.getMessage());
        }
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Map<String, String> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return errors;
    }
}
