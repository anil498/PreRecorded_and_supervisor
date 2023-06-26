package com.VideoPlatform.Controller;

import com.VideoPlatform.Services.FeatureService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.VideoPlatform.Constant.RequestMappings;
import com.VideoPlatform.Entity.FeatureEntity;
import com.VideoPlatform.Repository.FeatureRepository;
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

import static org.springframework.http.ResponseEntity.ok;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping(RequestMappings.APICALLFEATURE)
public class FeatureController {

    @Autowired
    FeatureRepository featureRepository;
    @Autowired
    FeatureService featureService;

    @PostMapping("/Create")
    public ResponseEntity<?> createFeature(@RequestBody FeatureEntity featureEntity, HttpServletRequest request) {
        featureService.createFeature(featureEntity);
        Map<String,String> result = new HashMap<>();
        result.put("status_code","200");
        result.put("msg", "Feature created!");
        return ok(result);
    }
    @GetMapping("/GetAll")
    public ResponseEntity<List<FeatureEntity>> featureList(HttpServletRequest request , HttpServletResponse response) {
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
        return featureService.updateFeature(featureEntity);
    }

    @DeleteMapping("/Delete/{featureId}")
    public ResponseEntity<?> deleteAccess(@PathVariable Integer featureId, HttpServletRequest request){
        featureService.deleteFeature(featureId);
        Map<String,String> result = new HashMap<>();
        result.put("status_code","200");
        result.put("msg", "Feature deleted!");
        return ok(result);
    }

}


