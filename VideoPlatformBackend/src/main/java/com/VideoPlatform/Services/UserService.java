package com.VideoPlatform.Services;

import com.VideoPlatform.Entity.UserEntity;

import java.util.List;

public interface UserService {
    List<UserEntity> getAllUsers();
    List<UserEntity> getAllChild(Integer id);
    UserEntity getUserById(Integer id);
    UserEntity createUser(UserEntity user);
    UserEntity updateUser(UserEntity user, Integer id);
    String deleteUser(Integer userId);
}
