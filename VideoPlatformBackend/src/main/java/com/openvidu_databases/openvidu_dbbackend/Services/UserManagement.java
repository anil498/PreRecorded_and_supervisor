package com.openvidu_databases.openvidu_dbbackend.Services;

import com.openvidu_databases.openvidu_dbbackend.Entity.UserEntity;
import org.springframework.http.ResponseEntity;

public interface UserManagement {
    ResponseEntity<UserEntity> getUserDetails(String userid);
}
