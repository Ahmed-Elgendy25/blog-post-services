package com.blogpostapp.blogpost.controllers;

import com.blogpostapp.blogpost.dto.PostDTO;
import com.blogpostapp.blogpost.dto.PostHateoasDTO;
import com.blogpostapp.blogpost.dto.PostSummaryDTO;
import com.blogpostapp.blogpost.entity.PostEntity;
import com.blogpostapp.blogpost.entity.UserEntity;
import com.blogpostapp.blogpost.services.PostServiceImp;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;


@RestController
@RequestMapping("/api/posts")
public class PostsController {

    private static final String SORT_BY_DATE = "date";
    private static final String DEFAULT_AUTHOR_NAME = "Unknown Author";
    
    private final PostServiceImp postServices;

    public PostsController(PostServiceImp postServices) {
        this.postServices = postServices;
    }

    @GetMapping("/paginated")
    public ResponseEntity<Page<PostSummaryDTO>> getPaginatedPosts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "date") String sortBy,
            @RequestParam(defaultValue = "desc") String direction) {
    
        Sort.Direction sortDirection = getSortDirection(direction);
        List<PostEntity> sortedPosts = getSortedNonCollaboratedPosts(sortBy, sortDirection);
        
        List<PostSummaryDTO> paginatedPosts = getPaginatedPostSummaries(sortedPosts, page, size);
        
        Page<PostSummaryDTO> pageResponse = new PageImpl<>(
            paginatedPosts,
            PageRequest.of(page, size, Sort.by(sortDirection, sortBy)),
            sortedPosts.size()
        );
    
        return ResponseEntity.ok(pageResponse);
    }
    
    @PostMapping("/create-article")
    @PreAuthorize("hasAuthority('author')")
    public ResponseEntity<?> uploadPost(@RequestBody PostDTO post) {
        try {
            validatePostRequest(post);
            
            if (postServices.existsByContent(post.content())) {
                return ResponseEntity.badRequest().body("A post with this content already exists");
            }
            
            PostEntity postEntity = createPostEntity(post);
            PostEntity savedPost = postServices.uploadPost(postEntity);
            PostDTO responseDto = convertToDTO(savedPost);
            
            return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
            
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                               .body("Error creating post: " + e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getPostById(@PathVariable Integer id) {
        try {
            return postServices.getPostById(id)
                    .map(post -> ResponseEntity.ok().body(convertToDTO(post)))
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                               .body("Error retrieving post: " + e.getMessage());
        }
    }

    @GetMapping("/{id}/hateoas")
    public ResponseEntity<EntityModel<PostHateoasDTO>> getPostWithHateoas(@PathVariable Integer id) {
        try {
            return postServices.getPostById(id)
                .map(post -> {
                    PostHateoasDTO responseDto = convertToHateoasDTO(post);
                    EntityModel<PostHateoasDTO> postModel = EntityModel.of(responseDto);
                    addPostHateoasLinks(postModel, post);
                    return ResponseEntity.ok(postModel);
                })
                .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
   
    @PostMapping("/{id}/like")
    @PreAuthorize("hasAnyAuthority('author', 'user')")
    public ResponseEntity<EntityModel<PostDTO>> addLikes(@PathVariable Integer id) {
        try {
            return postServices.getPostById(id)
                .map(post -> {
                    post.setLikes(post.getLikes() + 1);
                    PostEntity updatedPost = postServices.uploadPost(post);
                    PostDTO responseDto = convertToDTO(updatedPost);
                    
                    EntityModel<PostDTO> postModel = EntityModel.of(responseDto);
                    addPostLinks(postModel, updatedPost);
                    
                    return ResponseEntity.ok(postModel);
                })
                .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    // Endpoint to get comments for a specific post (redirects to CommentController)
    @GetMapping("/{id}/comments")
    public ResponseEntity<?> getPostComments(@PathVariable Integer id,
                                           @RequestParam(defaultValue = "0") int page,
                                           @RequestParam(defaultValue = "10") int size) {
        // This endpoint provides a redirect to the comments API
        try {
            if (!postServices.getPostById(id).isPresent()) {
                return ResponseEntity.notFound().build();
            }
            
            // Return HATEOAS link to the actual comments endpoint
            EntityModel<String> response = EntityModel.of("Use the 'comments' link to access comments for this post");
            response.add(linkTo(PostsController.class)
                .slash("comments").slash("post").slash(id)
                .withRel("comments"));
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
   
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Map<String, String> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return errors;
    }
    
    // Helper methods
    private Sort.Direction getSortDirection(String direction) {
        return "asc".equalsIgnoreCase(direction) ? Sort.Direction.ASC : Sort.Direction.DESC;
    }
    
    private List<PostEntity> getSortedNonCollaboratedPosts(String sortBy, Sort.Direction sortDirection) {
        return postServices.getAllPosts().stream()
                .filter(post -> post.getCollaborators().isEmpty())
                .sorted((a, b) -> SORT_BY_DATE.equals(sortBy) ? 
                    sortDirection.isAscending() ? 
                        a.getDate().compareTo(b.getDate()) : 
                        b.getDate().compareTo(a.getDate()) : 0)
                .collect(Collectors.toList());
    }
    
    private List<PostSummaryDTO> getPaginatedPostSummaries(List<PostEntity> posts, int page, int size) {
        int start = Math.min(page * size, posts.size());
        int end = Math.min(start + size, posts.size());
        
        return posts.subList(start, end).stream()
                .map(this::convertToSummaryDTO)
                .collect(Collectors.toList());
    }
    
    private PostSummaryDTO convertToSummaryDTO(PostEntity post) {
        String authorName = post.getAuthor() != null ? 
            post.getAuthor().getFirstName() + " " + post.getAuthor().getLastName() : 
            DEFAULT_AUTHOR_NAME;
            
        return new PostSummaryDTO(
            post.getId(),
            post.getTitle(),
            post.getDate(),
            post.getDurationRead(),
            post.getAuthorId(),
            authorName,
            post.getPostImg()
        );
    }
    
    private void validatePostRequest(PostDTO post) {
        if (post == null || post.authorId() == null) {
            throw new IllegalArgumentException("Post and author ID are required");
        }
    }
    
    private PostEntity createPostEntity(PostDTO post) {
        PostEntity postEntity = new PostEntity();
        postEntity.setContent(post.content());
        postEntity.setTitle(post.title());
        postEntity.setSubTitle(post.subTitle());
        postEntity.setDurationRead(post.durationRead());
        postEntity.setPostImg(post.postImg());
        
        UserEntity author = new UserEntity();
        author.setId(post.authorId());
        postEntity.setAuthor(author);
        
        return postEntity;
    }
    
    private PostDTO convertToDTO(PostEntity post) {
        return new PostDTO(
            post.getContent(),
            post.getAuthor().getId(),
            post.getDurationRead(),
            post.getPostImg(),
            post.getTitle(),
            post.getSubTitle(),
            post.getDate(),
            post.getLikes()
        );
    }
    
    private PostHateoasDTO convertToHateoasDTO(PostEntity post) {
        return new PostHateoasDTO(
            post.getId(),
            post.getContent(),
            post.getAuthor().getId(),
            post.getDurationRead(),
            post.getPostImg(),
            post.getTitle(),
            post.getSubTitle(),
            post.getDate(),
            post.getLikes()
        );
    }
    
    private void addPostHateoasLinks(EntityModel<PostHateoasDTO> postModel, PostEntity post) {
        // Self link
        postModel.add(linkTo(methodOn(PostsController.class)
            .getPostWithHateoas(post.getId())).withSelfRel());
        
        // Comments link - points to the CommentController endpoint
        postModel.add(linkTo(methodOn(CommentController.class)
            .getCommentsByPostId(post.getId(), 0, 10, true)).withRel("comments"));
        
        // Author link
        postModel.add(linkTo(methodOn(UserController.class)
            .userById(post.getAuthorId())).withRel("author"));
    }
    
    private void addPostLinks(EntityModel<PostDTO> postModel, PostEntity post) {
        // Self link
        postModel.add(linkTo(methodOn(PostsController.class)
            .getPostById(post.getId())).withSelfRel());
        
        // Link to like this post
        postModel.add(linkTo(methodOn(PostsController.class)
            .addLikes(post.getId())).withRel("like"));
        
        // Link to comments for this post
        postModel.add(linkTo(PostsController.class)
            .slash(post.getId()).slash("comments").withRel("comments"));
        
        // Link to comment count  
        postModel.add(linkTo(PostsController.class)
            .slash(post.getId()).slash("comments").slash("count").withRel("comment-count"));
        
        // Link to paginated posts
        postModel.add(linkTo(methodOn(PostsController.class)
            .getPaginatedPosts(0, 10, "date", "desc")).withRel("all-posts"));
    }
}
