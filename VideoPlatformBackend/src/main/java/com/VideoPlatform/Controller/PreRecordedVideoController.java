package com.VideoPlatform.Controller;

import com.VideoPlatform.Constant.RequestMappings;
import com.VideoPlatform.Entity.IcdcEntity;
import com.VideoPlatform.Entity.UserEntity;
import com.VideoPlatform.Services.CommonService;
import com.VideoPlatform.Services.PreRecordedVideoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping(RequestMappings.APICALLPRERECORDED)
public class PreRecordedVideoController {

    @Autowired
    private CommonService commonService;
    @Autowired
    private PreRecordedVideoService preRecordedVideoService;

    @PostMapping("/Save")
    public ResponseEntity<?> addPrerecordedVideo(@RequestParam(value = "prerecorded_video_file", required = false) MultipartFile file, HttpServletRequest request) {

        String authKey = request.getHeader("Authorization");
        String token = request.getHeader("Token");
//        if (!commonService.authorizationCheck(authKey, token, "")) {
//            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
//        }
        preRecordedVideoService.addPreRecordedVideo(file, authKey, token);
        return new ResponseEntity<>(commonService.responseData("200", "Video Saved!"), HttpStatus.OK);
    }
}
