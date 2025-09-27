package com.blogpostapp.blogpost.services;

import com.blogpostapp.blogpost.dao.CommentRepository;
import com.blogpostapp.blogpost.entity.CommentEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class CommentServiceImp implements CommentService {

    private final CommentRepository commentRepository;

    public CommentServiceImp(CommentRepository commentRepository) {
        this.commentRepository = commentRepository;
    }

    @Override
    @Transactional
    public CommentEntity createComment(CommentEntity comment) {
        if (comment == null) {
            throw new IllegalArgumentException("Comment cannot be null");
        }
        if (comment.getContent() == null || comment.getContent().trim().isEmpty()) {
            throw new IllegalArgumentException("Comment content cannot be empty");
        }
        if (comment.getPost() == null) {
            throw new IllegalArgumentException("Comment must be associated with a post");
        }
        if (comment.getAuthor() == null) {
            throw new IllegalArgumentException("Comment must have an author");
        }
        
        comment.setCreatedAt(LocalDateTime.now());
        comment.setLikes(0);
        
        return commentRepository.save(comment);
    }

    @Override
    public Optional<CommentEntity> getCommentById(Integer id) {
        return commentRepository.findById(id);
    }

    @Override
    public Page<CommentEntity> getCommentsByPostId(Integer postId, Pageable pageable) {
        return commentRepository.findByPostIdOrderByCreatedAtDesc(postId, pageable);
    }

    @Override
    public Page<CommentEntity> getTopLevelCommentsByPostId(Integer postId, Pageable pageable) {
        return commentRepository.findTopLevelCommentsByPostId(postId, pageable);
    }

    @Override
    public List<CommentEntity> getRepliesByParentCommentId(Integer parentCommentId) {
        return commentRepository.findRepliesByParentCommentId(parentCommentId);
    }

    @Override
    @Transactional
    public CommentEntity updateComment(Integer id, String content) {
        CommentEntity comment = commentRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Comment not found with id: " + id));
        
        if (content == null || content.trim().isEmpty()) {
            throw new IllegalArgumentException("Comment content cannot be empty");
        }
        
        comment.setContent(content);
        comment.setUpdatedAt(LocalDateTime.now());
        
        return commentRepository.save(comment);
    }

    @Override
    @Transactional
    public void deleteComment(Integer id) {
        if (!commentRepository.existsById(id)) {
            throw new IllegalArgumentException("Comment not found with id: " + id);
        }
        commentRepository.deleteById(id);
    }

    @Override
    @Transactional
    public CommentEntity likeComment(Integer id) {
        CommentEntity comment = commentRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Comment not found with id: " + id));
        
        comment.setLikes(comment.getLikes() + 1);
        
        return commentRepository.save(comment);
    }

    @Override
    public Long countCommentsByPostId(Integer postId) {
        return commentRepository.countByPostId(postId);
    }

    @Override
    public Page<CommentEntity> getCommentsByAuthorId(Integer authorId, Pageable pageable) {
        return commentRepository.findByAuthorIdOrderByCreatedAtDesc(authorId, pageable);
    }
}