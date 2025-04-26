package com.blogpostapp.blogpost.services;

import com.blogpostapp.blogpost.dto.LoginUserDTO;
import com.blogpostapp.blogpost.entity.UserEntity;

public interface UserService {
    public UserEntity registerUser(UserEntity user);
    // public Boolean loginUser(LoginUserDTO user);
    public Boolean userExistByEmail(String email);


}
