package com.blogpostapp.blogpost.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.blogpostapp.blogpost.dao.UserRepository;
import com.blogpostapp.blogpost.dto.LoginUserDTO;
import com.blogpostapp.blogpost.entity.UserEntity;

@Service
public class UserServiceImp implements UserService{
    private UserRepository userRepository;

    @Autowired
    public UserServiceImp(UserRepository theUserRepository) {
        userRepository = theUserRepository;
    }

    @Override
    public Boolean userExistByEmail(String email) {
        Optional<UserEntity> user = userRepository.findByEmail(email);
        return user.isPresent();
    }
    
    @Transactional
    @Override
    public UserEntity registerUser(UserEntity user) {
        List<UserEntity.UserType> type = new ArrayList<>();
        if (user.getType() != null) {
            type.addAll(user.getType());
        }
        user.setType(type);
        
        userRepository.save(user);
        return user;
    }





}
// @Override
// public Boolean loginUser(AuthUserDTO user) {
//     UserEntity userEmail = userRepository.findByEmail(user.email());
//     if(userEmail == null) {
//         return false;
//     }
//      return  user.password().equals(userEmail.getPassword());
// }
