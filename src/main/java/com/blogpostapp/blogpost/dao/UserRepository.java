package com.blogpostapp.blogpost.dao;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.blogpostapp.blogpost.entity.UserEntity;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Integer> {
    
    @Query("FROM UserEntity WHERE email = :email")
    Optional<UserEntity> findByEmail(@Param("email") String email);
    
    // Optional<UserEntity> findByUsername(String username);

    // Boolean existsByUsername(String username);
  
    // Boolean existsByEmail(String email);
}