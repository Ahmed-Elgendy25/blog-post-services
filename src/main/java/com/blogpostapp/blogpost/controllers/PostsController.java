package com.blogpostapp.blogpost.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.blogpostapp.blogpost.entity.PostEntity;
import com.blogpostapp.blogpost.services.PostServiceImp;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;



@RestController
@RequestMapping("/posts")
public class PostsController {

    private PostServiceImp postServices;
    


    @Autowired
    PostsController(PostServiceImp thePostService) {
        this.postServices = thePostService;
    }



    @PostMapping("/create-article")
    public ResponseEntity<PostEntity> uploadPost(
            @RequestBody PostEntity post,
            @RequestParam Integer authorId) {
        
        PostEntity savedPost = postServices.uploadPost(post, authorId);
        return new ResponseEntity<>(savedPost, HttpStatus.CREATED);
    }

    @GetMapping("/get-article/{id}")
    public Optional<PostEntity> getPostById(@PathVariable Integer id) {
        Optional<PostEntity> singlePost = postServices.getPostById(id);
        return singlePost;
    }
    




  
    
    
 
}
