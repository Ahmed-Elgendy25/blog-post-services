package com.blogpostapp.blogpost.services;

import java.util.Optional;

import com.blogpostapp.blogpost.dto.UserDTO;
import com.blogpostapp.blogpost.entity.UserEntity;

public interface UserService {
    public UserEntity registerUser(UserEntity user);
    // public Boolean loginUser(LoginUserDTO user);
    public Boolean userExistByEmail(String email);
    public Optional<UserDTO> getUserById(Integer id);
}
