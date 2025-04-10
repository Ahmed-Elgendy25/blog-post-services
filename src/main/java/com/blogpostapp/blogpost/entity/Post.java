package com.blogpostapp.blogpost.entity;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;


@Entity
@Table(name = "post")
public class Post {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @ManyToOne
    @JoinColumn(name = "author_id")
    private User author;
    
    private LocalDate date;
    
    @Column(name = "duration_read")
    private String durationRead;
    
    @Column(name = "post_img")
    private String postImg;
    
    @Lob
    @Column(name = "content")
    private String content;
    
    @ManyToMany(mappedBy = "collaboratedPosts")
    private Set<User> collaborators = new HashSet<>();

    public Post() {
    }

    public Post(User author, LocalDate date, String durationRead, String content) {
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
        return author.getId();
    }

    public void setAuthor(User author) {
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

    public void setCollaborators(Set<User> collaborators) {
        this.collaborators = collaborators;
    }

    @Override
    public String toString() {
        return "Post [id=" + id + ", author=" + author + ", date=" + date + ", durationRead=" + durationRead
                + ", content=" + content + "]";
    }
    
}

