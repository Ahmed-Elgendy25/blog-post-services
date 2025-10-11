package com.blogpostapp.blogpost.dao;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.blogpostapp.blogpost.entities.SubscribersEntity;

public interface SubscribersRepository extends JpaRepository<SubscribersEntity, Long> {

    boolean existsByEmailIgnoreCase(String email);

    Optional<SubscribersEntity> findByEmailIgnoreCase(String email);

    long deleteByEmailIgnoreCase(String email);

    @Query("select s.email from SubscribersEntity s")
    List<String> findAllEmails();
}