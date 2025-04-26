package com.blogpostapp.blogpost.entity;

import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.*;

@Entity
@Table(name = "user")
public class UserEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @Column(name = "first_name")
    private String firstName;
    
    @Column(name = "last_name")
    private String lastName;
    
    @Column(name = "email")
    private String email;
    
    @Column(name = "password")
    private String password;
    
    @Column(name = "user_img")
    private String userImg;
    
    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "ENUM('user','author')")
    private UserType type;
    
    @ManyToMany
    @JoinTable(
        name = "user_post",
        joinColumns = @JoinColumn(name = "author_id"),
        inverseJoinColumns = @JoinColumn(name = "post_id")
    )
    private Set<PostEntity> collaboratedPosts = new HashSet<>();


    public UserEntity() {
        
    }
    public UserEntity(String firstName, String lastName, String email, String password, UserType type) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
        this.type = type;
    }

    public enum UserType {
        user, author
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUserImg() {
        return userImg;
    }

    public void setUserImg(String userImg) {
        this.userImg = userImg;
    }

    public UserType getType() {
        return type;
    }

    public void setType(UserType type) {
        this.type = type;
    }

    public Set<PostEntity> getCollaboratedPosts() {
        return collaboratedPosts;
    }

    public void setCollaboratedPosts(Set<PostEntity> collaboratedPosts) {
        this.collaboratedPosts = collaboratedPosts;
    }

    @Override
    public String toString() {
        return "User [id=" + id + ", firstName=" + firstName + ", lastName=" + lastName + ", email=" + email
                + ", password=" + password + ", type=" + type + ", collaboratedPosts=" + collaboratedPosts + "]";
    }
    
    // Constructors, getters, setters
}

