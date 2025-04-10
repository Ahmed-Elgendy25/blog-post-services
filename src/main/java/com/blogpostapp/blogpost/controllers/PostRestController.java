package com.blogpostapp.blogpost.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.blogpostapp.blogpost.entity.Post;
import com.blogpostapp.blogpost.services.PostServicesImp;

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
public class PostRestController {

    private PostServicesImp postServices;
    


    @Autowired
    PostRestController(PostServicesImp thePostService) {
        this.postServices = thePostService;
    }



    @PostMapping("/create-article")
    public ResponseEntity<Post> uploadPost(
            @RequestBody Post post,
            @RequestParam Integer authorId) {
        
        Post savedPost = postServices.uploadPost(post, authorId);
        return new ResponseEntity<>(savedPost, HttpStatus.CREATED);
    }

    @GetMapping("/get-article/{id}")
    public Optional<Post> getPostById(@PathVariable Integer id) {
        Optional<Post> singlePost = postServices.getPostById(id);
        return singlePost;
    }
    




  
    
    
 
}
