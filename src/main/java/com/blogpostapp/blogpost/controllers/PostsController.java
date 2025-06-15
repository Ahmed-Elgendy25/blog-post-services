package com.blogpostapp.blogpost.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.blogpostapp.blogpost.dao.UserRepository;
import com.blogpostapp.blogpost.dto.PostDTO;
import com.blogpostapp.blogpost.dto.PostSummaryDTO;
import com.blogpostapp.blogpost.entity.PostEntity;
import com.blogpostapp.blogpost.entity.UserEntity;
import com.blogpostapp.blogpost.services.PostServiceImp;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;

@RestController
@RequestMapping("/api/posts")
public class PostsController {

    private final PostServiceImp postServices;


    @Autowired
    public PostsController(PostServiceImp postService) {
        this.postServices = postService;
    }

    @GetMapping("/paginated")
    public ResponseEntity<?> getPaginatedPosts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "date") String sortBy,
            @RequestParam(defaultValue = "desc") String direction) {
    
        Sort.Direction sortDirection = direction.equalsIgnoreCase("asc") ? 
            Sort.Direction.ASC : Sort.Direction.DESC;
    
        // Filter non-collaborated posts first
        List<PostEntity> allNonCollaboratedPosts = postServices.getAllPosts().stream()
            .filter(post -> post.getCollaborators().isEmpty())
            .sorted((a, b) -> {
                if (sortBy.equals("date")) {
                    return sortDirection.isAscending() ? 
                        a.getDate().compareTo(b.getDate()) : 
                        b.getDate().compareTo(a.getDate());
                }
                return 0; 
            })
            .collect(Collectors.toList());
    
        int start = Math.min(page * size, allNonCollaboratedPosts.size());
        int end = Math.min(start + size, allNonCollaboratedPosts.size());
    
        List<PostSummaryDTO> paginatedAndMapped = allNonCollaboratedPosts.subList(start, end).stream()
            .map(post -> new PostSummaryDTO(
                post.getId(),
                post.getTitle(),
                post.getDate(),
                post.getDurationRead(),
                post.getAuthorId(),
                post.getAuthor() != null ? 
                    post.getAuthor().getFirstName() + " " + post.getAuthor().getLastName() : 
                    null,
                post.getPostImg()
            ))
            .collect(Collectors.toList());
    
        Page<PostSummaryDTO> pageResponse = new PageImpl<>(
            paginatedAndMapped,
            PageRequest.of(page, size, Sort.by(sortDirection, sortBy)),
            allNonCollaboratedPosts.size()
        );
    
        return ResponseEntity.ok(pageResponse);
    }
    
    @PostMapping("/create-article")
    @PreAuthorize("hasAuthority('author')")
    public ResponseEntity<?> uploadPost(@RequestBody PostDTO post) {
        try {
            if (post == null || post.authorId() == null) {
                return ResponseEntity.badRequest().body("Post and author ID are required");
            }
           
            // Check if a post with the same content already exists
            if (postServices.existsByContent(post.content())) {
                return ResponseEntity.badRequest().body("A post with this content already exists");
            }
            
            // Create post entity from DTO
            PostEntity postEntity = new PostEntity();
            //Create author entity 
            UserEntity author = new UserEntity();
            postEntity.setContent(post.content());
            postEntity.setTitle(post.title());
            postEntity.setDurationRead(post.durationRead());
            
            // Create a temporary author reference
            author.setId(post.authorId());
            postEntity.setAuthor(author);
            
          
      
                postEntity.setPostImg(post.postImg());
          

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

    @GetMapping("/{id}")
    public ResponseEntity<?> getPostById(@PathVariable Integer id) {
        try {
            Optional<PostEntity> post = postServices.getPostById(id);
            return post.map(p -> ResponseEntity.ok().body(
                new PostDTO(
                    p.getContent(),
                    p.getAuthor().getId(),  
                    p.getDurationRead(),
                    p.getPostImg(),
                    p.getTitle(),
                    p.getDate()
                )
            )).orElse(ResponseEntity.notFound().build());
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
            errors.put(fieldName,   errorMessage);
        });
        return errors;
    }
}
