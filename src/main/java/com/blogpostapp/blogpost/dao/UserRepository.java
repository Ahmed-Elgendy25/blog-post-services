package com.blogpostapp.blogpost.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.blogpostapp.blogpost.entity.User;

public interface UserRepository extends JpaRepository<User, Integer> {
    
    
}
