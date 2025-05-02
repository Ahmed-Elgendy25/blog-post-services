package com.blogpostapp.blogpost.entity;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import jakarta.persistence.*;

@Entity
@Table(name = "users")
public class UserEntity implements UserDetails {
    
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
    
    @ElementCollection
    @CollectionTable(
        name = "user_type",
        joinColumns = @JoinColumn(name = "user_id")
    )
    @Enumerated(EnumType.STRING)
    @Column(name = "type")
    private List<UserType> type;
    
    @ManyToMany
    @JoinTable(
        name = "user_post",
        joinColumns = @JoinColumn(name = "author_id"),
        inverseJoinColumns = @JoinColumn(name = "post_id")
    )
    private Set<PostEntity> collaboratedPosts = new HashSet<>();

    public enum UserType {
        user, author
    }
    public UserEntity() {
        
    }
    public UserEntity(String firstName, String lastName, String email, String password, List<UserType> type) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
        this.type = type;
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

    public List<UserType> getType() {
        return type;
    }

    public void setType(List<UserType> type) {
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
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
      return List.of( new SimpleGrantedAuthority(type.toString()));
    }
    @Override
    public String getUsername() {
        return email;
    }
    
}

