package com.blogpostapp.blogpost.controllers;

import com.blogpostapp.blogpost.dto.CommentRequestDTO;
import com.blogpostapp.blogpost.dto.ReplyResponseDTO;
import com.blogpostapp.blogpost.entity.CommentEntity;
import com.blogpostapp.blogpost.entity.PostEntity;
import com.blogpostapp.blogpost.entity.UserEntity;
import com.blogpostapp.blogpost.services.CommentServiceImp;
import com.blogpostapp.blogpost.services.PostServiceImp;

import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@RestController
@RequestMapping("/api/replies")
public class ReplyController {

    private final CommentServiceImp commentService;
    private final PostServiceImp postService;

    public ReplyController(CommentServiceImp commentService, PostServiceImp postService) {
        this.commentService = commentService;
        this.postService = postService;
    }

    @PostMapping
    @PreAuthorize("hasAnyAuthority('author', 'user')")
    public ResponseEntity<EntityModel<ReplyResponseDTO>> createReply(@RequestBody CommentRequestDTO replyRequest) {
        try {
            validateReplyRequest(replyRequest);
            
            CommentEntity reply = createReplyEntity(replyRequest);
            CommentEntity savedReply = commentService.createComment(reply);
            ReplyResponseDTO responseDto = convertToReplyResponseDTO(savedReply);
            
            EntityModel<ReplyResponseDTO> replyModel = EntityModel.of(responseDto);
            addReplyLinks(replyModel, savedReply);
            
            return ResponseEntity.status(HttpStatus.CREATED).body(replyModel);
            
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<ReplyResponseDTO>> getReplyById(@PathVariable Integer id) {
        try {
            Optional<CommentEntity> reply = commentService.getCommentById(id);
            
            if (reply.isPresent() && reply.get().isReply()) {
                ReplyResponseDTO responseDto = convertToReplyResponseDTO(reply.get());
                EntityModel<ReplyResponseDTO> replyModel = EntityModel.of(responseDto);
                addReplyLinks(replyModel, reply.get());
                
                return ResponseEntity.ok(replyModel);
            }
            
            return ResponseEntity.notFound().build();
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{parentId}/replies")
    public ResponseEntity<List<EntityModel<ReplyResponseDTO>>> getChildReplies(@PathVariable Integer parentId) {
        try {
            List<CommentEntity> childReplies = commentService.getRepliesByParentCommentId(parentId);
            
            List<EntityModel<ReplyResponseDTO>> replyModels = childReplies.stream()
                .map(reply -> {
                    ReplyResponseDTO dto = convertToReplyResponseDTO(reply);
                    EntityModel<ReplyResponseDTO> model = EntityModel.of(dto);
                    addReplyLinks(model, reply);
                    return model;
                })
                .collect(Collectors.toList());
            
            return ResponseEntity.ok(replyModels);
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/{id}/like")
    @PreAuthorize("hasAnyAuthority('author', 'user')")
    public ResponseEntity<EntityModel<ReplyResponseDTO>> likeReply(@PathVariable Integer id) {
        try {
            CommentEntity likedReply = commentService.likeComment(id);
            ReplyResponseDTO responseDto = convertToReplyResponseDTO(likedReply);
            
            EntityModel<ReplyResponseDTO> replyModel = EntityModel.of(responseDto);
            addReplyLinks(replyModel, likedReply);
            
            return ResponseEntity.ok(replyModel);
            
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('author', 'user')")
    public ResponseEntity<Void> deleteReply(@PathVariable Integer id) {
        try {
            commentService.deleteComment(id);
            return ResponseEntity.noContent().build();
            
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Helper methods
    private void validateReplyRequest(CommentRequestDTO request) {
        if (request == null) {
            throw new IllegalArgumentException("Reply request cannot be null");
        }
        if (request.content() == null || request.content().trim().isEmpty()) {
            throw new IllegalArgumentException("Reply content cannot be empty");
        }
        if (request.postId() == null) {
            throw new IllegalArgumentException("Post ID is required");
        }
        if (request.authorId() == null) {
            throw new IllegalArgumentException("Author ID is required");
        }
        if (request.parentCommentId() == null) {
            throw new IllegalArgumentException("Parent comment ID is required for replies");
        }
    }

    private CommentEntity createReplyEntity(CommentRequestDTO request) {
        CommentEntity reply = new CommentEntity();
        reply.setContent(request.content());
        
        // Set post
        PostEntity post = postService.getPostById(request.postId())
            .orElseThrow(() -> new IllegalArgumentException("Post not found"));
        reply.setPost(post);
        
        // Set author
        UserEntity author = new UserEntity();
        author.setId(request.authorId());
        reply.setAuthor(author);
        
        // Set parent comment (required for replies)
        CommentEntity parentComment = commentService.getCommentById(request.parentCommentId())
            .orElseThrow(() -> new IllegalArgumentException("Parent comment not found"));
        reply.setParentComment(parentComment);
        
        return reply;
    }

    private ReplyResponseDTO convertToReplyResponseDTO(CommentEntity reply) {
        return new ReplyResponseDTO(
            reply.getId(),
            reply.getContent(),
            reply.getAuthorId(),
            reply.getCreatedAt().toLocalDate(),
            reply.getLikes(),
            reply.getParentCommentId()
        );
    }

    private void addReplyLinks(EntityModel<ReplyResponseDTO> replyModel, CommentEntity reply) {
        // Self link
        replyModel.add(linkTo(methodOn(ReplyController.class)
            .getReplyById(reply.getId())).withSelfRel());
        
        // Link to parent comment
        replyModel.add(linkTo(methodOn(CommentController.class)
            .getCommentById(reply.getParentCommentId())).withRel("comment"));
        
        // Link to author
        replyModel.add(linkTo(methodOn(UserController.class)
            .userById(reply.getAuthorId())).withRel("author"));
        
        // Link to child replies (nested replies)
        replyModel.add(linkTo(methodOn(ReplyController.class)
            .getChildReplies(reply.getId())).withRel("childReplies"));
        
        // Action links
        replyModel.add(linkTo(methodOn(ReplyController.class)
            .likeReply(reply.getId())).withRel("like"));
        
        replyModel.add(linkTo(methodOn(ReplyController.class)
            .deleteReply(reply.getId())).withRel("delete"));
    }
}