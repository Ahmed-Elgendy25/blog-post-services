package com.blogpostapp.blogpost.dto;

import java.util.List;

import com.blogpostapp.blogpost.entity.UserEntity.UserType;


public record RegisterUserDTO(String email, String password, String firstName, String lastName, String userImg, List<UserType> type) {

}
