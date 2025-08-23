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
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id", nullable = false)
    private UserEntity author;
    
    @Column(name = "date")
    private LocalDate date;
    
    @Column(name = "duration_read",columnDefinition = "VARCHAR")
    private String durationRead;
    
    @Column(name = "title",columnDefinition = "TEXT")
    private String title;
    @Column(name="sub_title",columnDefinition = "TEXT")
    private String subTitle;
  
    @Column(name = "banner",columnDefinition = "TEXT")
    private String postImg;
    
    @Column(name = "content",columnDefinition = "TEXT")
    private String content;
    
    @ManyToMany
    private Set<UserEntity> collaboratingUsers  = new HashSet<>();

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


    public void setCollaborators(Set<UserEntity> collaborators) {
        this.collaboratingUsers = collaborators;
    }

    @Override
    public String toString() {
        return "Post [id=" + id + ", authorName=" + author.getFirstName() + " " + author.getLastName() + ", date=" + date + ", durationRead=" + durationRead
                + ", content=" + content + "]";
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public UserEntity getAuthor() {
        return author;
    }

    public String getTitle() {
        return title;
    }

    public Set<UserEntity> getCollaborators() {
        return collaboratingUsers ;
    }

    public String getSubTitle() {
        return subTitle;
    }

    public void setSubTitle(String subTitle) {
        this.subTitle = subTitle;
    }

    public void setCollaboratingUsers(Set<UserEntity> collaboratingUsers) {
        this.collaboratingUsers = collaboratingUsers;
    }
    
}

