package com.VideoPlatform.Services;

import com.VideoPlatform.Entity.UserEntity;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface UserService {
    List<UserEntity> getAllUsers();
    List<UserEntity> getAllChild(Integer id);
    UserEntity getUserById(Integer id);
    UserEntity createUser(UserEntity user,String authKey,String token);
    UserEntity createUserZero(UserEntity user);
    UserEntity updateUser(UserEntity user);
    String deleteUser(Integer userId);
    public ResponseEntity<?> loginService(String loginId, String password, int authId);
}
