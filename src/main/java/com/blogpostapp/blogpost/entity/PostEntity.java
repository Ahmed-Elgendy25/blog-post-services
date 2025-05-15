package com.blogpostapp.blogpost.entity;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;


@Entity
@Table(name = "posts")
public class PostEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "author_id", nullable = false)
    private UserEntity author;
    
    @Column(name = "date")
    private LocalDate date;
    
    @Column(name = "duration_read")
    private String durationRead;

    @Lob
    @Column(name = "post_img",columnDefinition = "BLOB")
    private String postImg;
    
    @Lob
    @Column(name = "content")
    private String content;
    
    @ManyToMany(mappedBy = "collaboratedPosts")
    private Set<UserEntity> collaborators = new HashSet<>();

    public PostEntity() {
    }

    public PostEntity(UserEntity author, LocalDate date, String durationRead, String content) {
        this.author = author;
        this.date = date;
        this.durationRead = durationRead;
        this.content = content;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getAuthorId() {
        return author != null ? author.getId() : null;
    }

    public void setAuthor(UserEntity author) {
        this.author = author;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public String getDurationRead() {
        return durationRead;
    }

    public void setDurationRead(String durationRead) {
        this.durationRead = durationRead;
    }

    public String getPostImg() {
        return postImg;
    }

    public void setPostImg(String postImg) {
        this.postImg = postImg;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    // public Set<User> getCollaborators() {
    //     return collaborators;
    // }

    public void setCollaborators(Set<UserEntity> collaborators) {
        this.collaborators = collaborators;
    }

    @Override
    public String toString() {
        return "Post [id=" + id + ", author=" + author + ", date=" + date + ", durationRead=" + durationRead
                + ", content=" + content + "]";
    }
    
}

