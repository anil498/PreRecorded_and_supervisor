package com.VideoPlatform.Controller;

import com.VideoPlatform.Entity.UserEntity;
import com.VideoPlatform.Services.CommonService;
import com.VideoPlatform.Services.FeatureService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.VideoPlatform.Constant.RequestMappings;
import com.VideoPlatform.Entity.FeatureEntity;
import com.VideoPlatform.Repository.FeatureRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.springframework.http.ResponseEntity.ok;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping(RequestMappings.APICALLFEATURE)
public class FeatureController {

    @Autowired
    private FeatureRepository featureRepository;
    @Autowired
    private FeatureService featureService;
    @Autowired
    private CommonService commonService;

    @PostMapping("/Create")
    public ResponseEntity<?> createFeature(@RequestBody FeatureEntity featureEntity, HttpServletRequest request) {
        String authKey = request.getHeader("Authorization");
        String token = request.getHeader("Token");

        if(!commonService.authorizationCheck(authKey,token,"manage_platform_feature")){
            return  new ResponseEntity<UserEntity>(HttpStatus.UNAUTHORIZED);
        }
        if(featureService.createFeature(featureEntity,authKey,token)==null){
            return  new ResponseEntity<>(commonService.responseData("401","Feature created but not updated to user"),HttpStatus.UNAUTHORIZED);
        }
        Map<String,String> result = new HashMap<>();
        result.put("status_code","200");
        result.put("msg", "Feature created!");
        return ok(result);
    }
    @GetMapping("/GetAll")
    public ResponseEntity<List<FeatureEntity>> featureList(HttpServletRequest request , HttpServletResponse response) {
        String authKey = request.getHeader("Authorization");
        String token = request.getHeader("Token");

        if(!commonService.authorizationCheck(authKey,token,"manage_platform_feature")){
            return  new ResponseEntity<List<FeatureEntity>>(HttpStatus.UNAUTHORIZED);
        }
        return ResponseEntity.ok(featureService.getAllFeatures());
    }
    @GetMapping("/GetList")
    public ResponseEntity<List<Object>>getIdList(HttpServletRequest request , HttpServletResponse response) throws JsonProcessingException {
//        ObjectMapper obj = new ObjectMapper();
//        String res = obj.writeValueAsString(featureRepository.findList());
//        return ResponseEntity.ok(res);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .body(featureRepository.findList());
    }

    @PutMapping("/Update")
    public ResponseEntity<?> updateFeature(@RequestBody String featureEntity, HttpServletRequest request){
        String authKey = request.getHeader("Authorization");
        String token = request.getHeader("Token");

        if(!commonService.authorizationCheck(authKey,token,"manage_platform_feature")){
            return  new ResponseEntity<List<FeatureEntity>>(HttpStatus.UNAUTHORIZED);
        }
        return featureService.updateFeature(featureEntity);
    }

    @DeleteMapping("/Delete/{featureId}")
    public ResponseEntity<?> deleteAccess(@PathVariable Integer featureId, HttpServletRequest request){
        String authKey = request.getHeader("Authorization");
        String token = request.getHeader("Token");

        if(!commonService.authorizationCheck(authKey,token,"manage_platform_feature")){
            return  new ResponseEntity<List<FeatureEntity>>(HttpStatus.UNAUTHORIZED);
        }
        featureService.deleteFeature(featureId);
        Map<String,String> result = new HashMap<>();
        result.put("status_code","200");
        result.put("msg", "Feature deleted!");
        return ok(result);
    }

}


