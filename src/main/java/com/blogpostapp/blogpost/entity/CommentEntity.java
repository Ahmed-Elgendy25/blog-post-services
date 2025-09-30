package com.blogpostapp.blogpost.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "comments")
public class CommentEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;
    
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @Column(nullable = false)
    private Integer likes = 0;
    
    // Many comments belong to one post
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private PostEntity post;
    
    // Many comments belong to one author (user)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id", nullable = false)
    private UserEntity author;
    
    // Self-referencing relationship for replies
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_comment_id")
    private CommentEntity parentComment;
    
    // One comment can have many replies
    @OneToMany(mappedBy = "parentComment", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<CommentEntity> replies = new ArrayList<>();
    
    // Constructors
    public CommentEntity() {
        this.createdAt = LocalDateTime.now();
        this.likes = 0;
    }
    
    public CommentEntity(String content, PostEntity post, UserEntity author) {
        this();
        this.content = content;
        this.post = post;
        this.author = author;
    }
    
    public CommentEntity(String content, PostEntity post, UserEntity author, CommentEntity parentComment) {
        this(content, post, author);
        this.parentComment = parentComment;
    }
    
    // Getters and Setters
    public Integer getId() {
        return id;
    }
    
    public void setId(Integer id) {
        this.id = id;
    }
    
    public String getContent() {
        return content;
    }
    
    public void setContent(String content) {
        this.content = content;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    public Integer getLikes() {
        return likes;
    }
    
    public void setLikes(Integer likes) {
        this.likes = likes;
    }
    
    public PostEntity getPost() {
        return post;
    }
    
    public void setPost(PostEntity post) {
        this.post = post;
    }
    
    public UserEntity getAuthor() {
        return author;
    }
    
    public void setAuthor(UserEntity author) {
        this.author = author;
    }
    
    public CommentEntity getParentComment() {
        return parentComment;
    }
    
    public void setParentComment(CommentEntity parentComment) {
        this.parentComment = parentComment;
    }
    
    public List<CommentEntity> getReplies() {
        return replies;
    }
    
    public void setReplies(List<CommentEntity> replies) {
        this.replies = replies;
    }
    
    // Helper methods
    public void addReply(CommentEntity reply) {
        replies.add(reply);
        reply.setParentComment(this);
    }
    
    public void removeReply(CommentEntity reply) {
        replies.remove(reply);
        reply.setParentComment(null);
    }
    
    public boolean isReply() {
        return parentComment != null;
    }
    
    public boolean hasReplies() {
        return !replies.isEmpty();
    }
    
    public int getReplyCount() {
        return replies.size();
    }
    
    // Increment likes
    public void incrementLikes() {
        this.likes++;
    }
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (likes == null) {
            likes = 0;
        }
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    @Override
    public String toString() {
        return "CommentEntity{" +
                "id=" + id +
                ", content='" + content + '\'' +
                ", createdAt=" + createdAt +
                ", likes=" + likes +
                ", authorId=" + (author != null ? author.getId() : null) +
                ", postId=" + (post != null ? post.getId() : null) +
                ", parentCommentId=" + (parentComment != null ? parentComment.getId() : null) +
                '}';
    }

    // Add these helper methods after existing getters/setters
    public Integer getPostId() {
        return post != null ? post.getId() : null;
    }

    public Integer getAuthorId() {
        return author != null ? author.getId() : null;
    }

    public Integer getParentCommentId() {
        return parentComment != null ? parentComment.getId() : null;
    }
}