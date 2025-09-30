package com.blogpostapp.blogpost.controllers;

import com.blogpostapp.blogpost.dto.CommentRequestDTO;
import com.blogpostapp.blogpost.dto.CommentResponseDTO;
import com.blogpostapp.blogpost.entity.CommentEntity;
import com.blogpostapp.blogpost.entity.PostEntity;
import com.blogpostapp.blogpost.entity.UserEntity;
import com.blogpostapp.blogpost.services.CommentServiceImp;
import com.blogpostapp.blogpost.services.PostServiceImp;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@RestController
@RequestMapping("/api/comments")
public class CommentController {

    private final CommentServiceImp commentService;
    private final PostServiceImp postService;
    private final PagedResourcesAssembler<CommentEntity> pagedResourcesAssembler;

    public CommentController(CommentServiceImp commentService, 
                           PostServiceImp postService,
                           PagedResourcesAssembler<CommentEntity> pagedResourcesAssembler) {
        this.commentService = commentService;
        this.postService = postService;
        this.pagedResourcesAssembler = pagedResourcesAssembler;
    }

    @PostMapping
    @PreAuthorize("hasAnyAuthority('author', 'user')")
    public ResponseEntity<EntityModel<CommentResponseDTO>> createComment(@RequestBody CommentRequestDTO commentRequest) {
        try {
            validateCommentRequest(commentRequest);
            
            CommentEntity comment = createCommentEntity(commentRequest);
            CommentEntity savedComment = commentService.createComment(comment);
            CommentResponseDTO responseDto = convertToResponseDTO(savedComment);
            
            EntityModel<CommentResponseDTO> commentModel = EntityModel.of(responseDto);
            addCommentLinks(commentModel, savedComment);
            
            return ResponseEntity.status(HttpStatus.CREATED).body(commentModel);
            
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<CommentResponseDTO>> getCommentById(@PathVariable Integer id) {
        try {
            Optional<CommentEntity> comment = commentService.getCommentById(id);
            
            if (comment.isPresent()) {
                CommentResponseDTO responseDto = convertToResponseDTO(comment.get());
                EntityModel<CommentResponseDTO> commentModel = EntityModel.of(responseDto);
                addCommentLinks(commentModel, comment.get());
                
                return ResponseEntity.ok(commentModel);
            }
            
            return ResponseEntity.notFound().build();
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/post/{postId}")
    public ResponseEntity<PagedModel<EntityModel<CommentResponseDTO>>> getCommentsByPostId(
            @PathVariable Integer postId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "false") boolean topLevelOnly) {
        
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<CommentEntity> comments;
            
            if (topLevelOnly) {
                comments = commentService.getTopLevelCommentsByPostId(postId, pageable);
            } else {
                comments = commentService.getCommentsByPostId(postId, pageable);
            }
            
            PagedModel<EntityModel<CommentResponseDTO>> pagedModel = pagedResourcesAssembler.toModel(
                comments, 
                comment -> {
                    CommentResponseDTO dto = convertToResponseDTO(comment);
                    EntityModel<CommentResponseDTO> model = EntityModel.of(dto);
                    addCommentLinks(model, comment);
                    return model;
                }
            );
            
            // Add self link for the collection
            pagedModel.add(linkTo(methodOn(CommentController.class)
                .getCommentsByPostId(postId, page, size, topLevelOnly))
                .withSelfRel());
            
            return ResponseEntity.ok(pagedModel);
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{parentId}/replies")
    public ResponseEntity<List<EntityModel<CommentResponseDTO>>> getReplies(@PathVariable Integer parentId) {
        try {
            List<CommentEntity> replies = commentService.getRepliesByParentCommentId(parentId);
            
            List<EntityModel<CommentResponseDTO>> replyModels = replies.stream()
                .map(reply -> {
                    CommentResponseDTO dto = convertToResponseDTO(reply);
                    EntityModel<CommentResponseDTO> model = EntityModel.of(dto);
                    addCommentLinks(model, reply);
                    return model;
                })
                .collect(Collectors.toList());
            
            return ResponseEntity.ok(replyModels);
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('author', 'user')")
    public ResponseEntity<EntityModel<CommentResponseDTO>> updateComment(
            @PathVariable Integer id, 
            @RequestBody String content) {
        
        try {
            CommentEntity updatedComment = commentService.updateComment(id, content);
            CommentResponseDTO responseDto = convertToResponseDTO(updatedComment);
            
            EntityModel<CommentResponseDTO> commentModel = EntityModel.of(responseDto);
            addCommentLinks(commentModel, updatedComment);
            
            return ResponseEntity.ok(commentModel);
            
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/{id}/like")
    @PreAuthorize("hasAnyAuthority('author', 'user')")
    public ResponseEntity<EntityModel<CommentResponseDTO>> likeComment(@PathVariable Integer id) {
        try {
            CommentEntity likedComment = commentService.likeComment(id);
            CommentResponseDTO responseDto = convertToResponseDTO(likedComment);
            
            EntityModel<CommentResponseDTO> commentModel = EntityModel.of(responseDto);
            addCommentLinks(commentModel, likedComment);
            
            return ResponseEntity.ok(commentModel);
            
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('author', 'user')")
    public ResponseEntity<Void> deleteComment(@PathVariable Integer id) {
        try {
            commentService.deleteComment(id);
            return ResponseEntity.noContent().build();
            
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/post/{postId}/count")
    public ResponseEntity<Long> getCommentCount(@PathVariable Integer postId) {
        try {
            Long count = commentService.countCommentsByPostId(postId);
            return ResponseEntity.ok(count);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Helper methods
    private void validateCommentRequest(CommentRequestDTO request) {
        if (request == null) {
            throw new IllegalArgumentException("Comment request cannot be null");
        }
        if (request.content() == null || request.content().trim().isEmpty()) {
            throw new IllegalArgumentException("Comment content cannot be empty");
        }
        if (request.postId() == null) {
            throw new IllegalArgumentException("Post ID is required");
        }
        if (request.authorId() == null) {
            throw new IllegalArgumentException("Author ID is required");
        }
    }

    private CommentEntity createCommentEntity(CommentRequestDTO request) {
        CommentEntity comment = new CommentEntity();
        comment.setContent(request.content());
        
        // Set post
        PostEntity post = postService.getPostById(request.postId())
            .orElseThrow(() -> new IllegalArgumentException("Post not found"));
        comment.setPost(post);
        
        // Set author - Create a UserEntity with the ID (we only need the ID for the relationship)
        UserEntity author = new UserEntity();
        author.setId(request.authorId());
        comment.setAuthor(author);
        
        // Set parent comment if it's a reply
        if (request.parentCommentId() != null) {
            CommentEntity parentComment = commentService.getCommentById(request.parentCommentId())
                .orElseThrow(() -> new IllegalArgumentException("Parent comment not found"));
            comment.setParentComment(parentComment);
        }
        
        return comment;
    }

    private CommentResponseDTO convertToResponseDTO(CommentEntity comment) {
        return new CommentResponseDTO(
            comment.getId(),
            comment.getContent(),
            comment.getAuthorId(),
            comment.getCreatedAt().toLocalDate(),
            comment.getLikes()
        );
    }

    private void addCommentLinks(EntityModel<CommentResponseDTO> commentModel, CommentEntity comment) {
        // Self link
        commentModel.add(linkTo(methodOn(CommentController.class)
            .getCommentById(comment.getId())).withSelfRel());
            
        
        // Link to post (assuming PostsController exists)
        commentModel.add(linkTo(methodOn(PostsController.class)
            .getPostById(comment.getPostId())).withRel("post"));
        
        // Link to author
        commentModel.add(linkTo(methodOn(UserController.class)
            .userById(comment.getAuthorId())).withRel("author"));
        
        // Link to replies - only for top-level comments
        if (comment.getParentComment() == null) {
            commentModel.add(linkTo(methodOn(CommentController.class)
                .getReplies(comment.getId())).withRel("replies"));
        }
    }
}