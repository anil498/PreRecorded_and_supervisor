package com.VideoPlatform.Services;

import com.VideoPlatform.Entity.UserEntity;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.http.ResponseEntity;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public interface UserService {
    List<UserEntity> getAllUsers();
    List<UserEntity> getAllChild(String token);
    UserEntity getUserById(Integer id);
    ResponseEntity<?> createUser(String user,String authKey,String token) throws JsonProcessingException;
    ResponseEntity<?> createUserZero(UserEntity user);
    ResponseEntity<?> updateUser(String params,String authKey);
    String deleteUser(Integer userId);
    ResponseEntity<?> loginService(String loginId, String password, int authId);
    Boolean checkLoginId(String loginId);
    void saveFilePathToFeature(String filePath,String loginId,String name);

    String getImage(Map<String, Object> params) throws IOException;
//    String resetPassword(String newPassword, String loginId, Integer userId);
}
