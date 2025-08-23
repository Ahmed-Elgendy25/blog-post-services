package com.blogpostapp.blogpost.entity;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.ArrayList;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
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

    @Column(name = "linkedin_profile")
    private String linkedInProfile;
    @Column(name = "twitter_profile")
    private String twitterProfile;
    @Column(name = "instagram_profile")
    private String instagramProfile;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "type")
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
        name = "user_types",
        joinColumns = @JoinColumn(name = "user_id")
    )
    private List<UserType> type = new ArrayList<>();
    
    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(
        name = "user_post",
        joinColumns = @JoinColumn(name = "author_id"),
        inverseJoinColumns = @JoinColumn(name = "post_id")
    
    )
    private Set<PostEntity> collaboratingPosts = new HashSet<>();

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
        return collaboratingPosts ;
    }

    public void setCollaboratedPosts(Set<PostEntity> collaboratedPosts) {
        this.collaboratingPosts  = collaboratedPosts;
    }

    @Override
    public String toString() {
        return "User [id=" + id + ", firstName=" + firstName + ", lastName=" + lastName + ", email=" + email
                + ", password=" + password + ", type=" + type +     
                "]";
    }
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return type.stream()
            .map(role -> new SimpleGrantedAuthority(role.name()))
            .collect(Collectors.toList());
    }
    
    @Override
    public String getUsername() {
        return email;
    }
    
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }


    public String getLinkedInProfile() {
        return linkedInProfile;
    }
    public String getTwitterProfile() {
        return twitterProfile;
    }
    public String getInstagramProfile() {
        return instagramProfile;
    }
    
    public void setLinkedInProfile(String linkedInProfile) {
        this.linkedInProfile = linkedInProfile;
    }

    public void setTwitterProfile(String twitterProfile) {
        this.twitterProfile = twitterProfile;
    }

    public void setInstagramProfile(String instagramProfile) {
        this.instagramProfile = instagramProfile;
    }

    public void setCollaboratingPosts(Set<PostEntity> collaboratingPosts) {
        this.collaboratingPosts = collaboratingPosts;
    }
    public Set<PostEntity> getCollaboratingPosts() {
        return collaboratingPosts;
    }
}

