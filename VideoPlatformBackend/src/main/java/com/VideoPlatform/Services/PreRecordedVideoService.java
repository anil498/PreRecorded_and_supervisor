package com.VideoPlatform.Services;

import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

public interface PreRecordedVideoService {
    ResponseEntity<?> addPreRecordedVideo(MultipartFile file, String authKey, String token);
}
