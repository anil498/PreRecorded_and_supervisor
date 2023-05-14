package com.openvidu_databases.openvidu_dbbackend.Controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.openvidu_databases.openvidu_dbbackend.Constant.RequestMappings;
import com.openvidu_databases.openvidu_dbbackend.Entity.FeatureEntity;
import com.openvidu_databases.openvidu_dbbackend.Entity.UserEntity;
import com.openvidu_databases.openvidu_dbbackend.Repository.FeatureRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping(RequestMappings.APIF)
public class FeatureController {

    @Autowired
    FeatureRepository featureRepository;

    @PostMapping("/create")
    public ResponseEntity<FeatureEntity> createUser(@RequestBody FeatureEntity feature, HttpServletRequest request, HttpServletResponse response) {
        return ResponseEntity.ok(featureRepository.save(feature));
    }
    @GetMapping("/getAll")
    public ResponseEntity<List<FeatureEntity>> featureList(HttpServletRequest request , HttpServletResponse response) {
        return ResponseEntity.ok(featureRepository.findAll());
    }
    @GetMapping("/getList")
    public ResponseEntity<List<Object>>getIdList(HttpServletRequest request , HttpServletResponse response) throws JsonProcessingException {
//        ObjectMapper obj = new ObjectMapper();
//        String res = obj.writeValueAsString(featureRepository.findList());
//        return ResponseEntity.ok(res);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .body(featureRepository.findList());
    }

}

