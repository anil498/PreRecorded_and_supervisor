package com.VideoPlatform.Controller;

import com.VideoPlatform.Entity.UserEntity;
import com.VideoPlatform.Repository.AccessRepository;
import com.VideoPlatform.Constant.RequestMappings;
import com.VideoPlatform.Entity.AccessEntity;
import com.VideoPlatform.Services.CommonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping(RequestMappings.APICALLACCESS)
public class AccessController {
    @Autowired
    AccessRepository accessRepository;
    @Autowired
    CommonService commonService;

    @PostMapping("/create")
    public ResponseEntity<?> createUser(@RequestBody AccessEntity access, HttpServletRequest request, HttpServletResponse response) {
        String authKey = request.getHeader("Authorization");
        String token = request.getHeader("Token");

        if(!commonService.authorizationCheck(authKey,token,"customer_management")){
            return  new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        return ResponseEntity.ok(accessRepository.save(access));
    }
}
