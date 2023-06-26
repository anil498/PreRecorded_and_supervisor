package com.VideoPlatform.Services;

import com.VideoPlatform.Entity.AccessEntity;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface AccessService {
    AccessEntity createAccess(AccessEntity accessEntity);
    ResponseEntity<?> updateAccess(String params);
    void deleteAccess(Integer accessId);
    List<AccessEntity> getAllAccess();
}
