package com.VideoPlatform.Services;

import com.VideoPlatform.Entity.AccessEntity;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface AccessService {
    String createAccess(AccessEntity accessEntity,String authKey,String token);
    ResponseEntity<?> updateAccess(String params);
    void deleteAccess(Integer accessId);
    List<AccessEntity> getAllAccess();
}
