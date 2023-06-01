package com.VideoPlatform.Services;

import com.VideoPlatform.Entity.UserEntity;
import org.springframework.http.ResponseEntity;

public interface UserManagement {
    ResponseEntity<UserEntity> getUserDetails(String userid);
}
