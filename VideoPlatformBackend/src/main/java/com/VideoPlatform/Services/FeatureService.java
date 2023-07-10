package com.VideoPlatform.Services;

import com.VideoPlatform.Entity.AccessEntity;
import com.VideoPlatform.Entity.FeatureEntity;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface FeatureService {
    String createFeature(FeatureEntity featureEntity,String authKey, String token);
    ResponseEntity<?> updateFeature(String params);
    void deleteFeature(Integer featureId);
    List<FeatureEntity> getAllFeatures();
}
