package com.blogpostapp.blogpost.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.blogpostapp.blogpost.entity.UserEntity;

public interface UserRepository extends JpaRepository<UserEntity, Integer> {
    
    @Query("FROM UserEntity WHERE email = :email")
    UserEntity findByEmail(@Param("email") String email);
    
 
}