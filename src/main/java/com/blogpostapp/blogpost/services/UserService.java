package com.blogpostapp.blogpost.services;

import com.blogpostapp.blogpost.dto.AuthUserDTO;
import com.blogpostapp.blogpost.entity.UserEntity;

public interface UserService {
    public UserEntity registerUser(UserEntity user);
    public Boolean loginUser(AuthUserDTO user);
    


}
