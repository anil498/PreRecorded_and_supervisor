package com.VideoPlatform.Controller;

import com.VideoPlatform.Repository.AccessRepository;
import com.VideoPlatform.Constant.RequestMappings;
import com.VideoPlatform.Entity.AccessEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping(RequestMappings.APIA)
public class AccessController {
    @Autowired
    AccessRepository accessRepository;

    @PostMapping("/create")
    public ResponseEntity<AccessEntity> createUser(@RequestBody AccessEntity access, HttpServletRequest request, HttpServletResponse response) {
        return ResponseEntity.ok(accessRepository.save(access));
    }
}
