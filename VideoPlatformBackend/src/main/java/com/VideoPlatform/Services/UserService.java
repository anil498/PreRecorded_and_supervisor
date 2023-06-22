package com.VideoPlatform.Services;

import com.VideoPlatform.Entity.UserEntity;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface UserService {
    List<UserEntity> getAllUsers();
    List<UserEntity> getAllChild(Integer id);
    UserEntity getUserById(Integer id);
    ResponseEntity<?> createUser(UserEntity user,String authKey,String token);
    UserEntity createUserZero(UserEntity user);
    ResponseEntity<?> updateUser(String params,String authKey);
    String deleteUser(Integer userId);
    ResponseEntity<?> loginService(String loginId, String password, int authId);
    Boolean checkLoginId(String loginId);
//    String resetPassword(String newPassword, String loginId, Integer userId);
}
