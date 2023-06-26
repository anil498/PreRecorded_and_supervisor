package com.VideoPlatform.Controller;

import com.VideoPlatform.Entity.UserEntity;
import com.VideoPlatform.Repository.AccessRepository;
import com.VideoPlatform.Constant.RequestMappings;
import com.VideoPlatform.Entity.AccessEntity;
import com.VideoPlatform.Services.AccessService;
import com.VideoPlatform.Services.CommonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

import static org.springframework.http.ResponseEntity.ok;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping(RequestMappings.APICALLACCESS)
public class AccessController {
    @Autowired
    AccessRepository accessRepository;
    @Autowired
    CommonService commonService;
    @Autowired
    AccessService accessService;

    @PostMapping("/Create")
    public ResponseEntity<?> createUser(@RequestBody AccessEntity accessEntity, HttpServletRequest request, HttpServletResponse response) {
        accessService.createAccess(accessEntity);
        Map<String,String> result = new HashMap<>();
        result.put("status_code","200");
        result.put("msg", "Access created!");
        return ok(result);
    }

    @GetMapping("/GetAll")
    public ResponseEntity<?> getAllAccess(HttpServletRequest request){
        return ok(accessService.getAllAccess());
    }

    @PutMapping("/Update")
    public ResponseEntity<?> updateUser(@RequestBody String accessEntity, HttpServletRequest request){
        return accessService.updateAccess(accessEntity);
    }

    @DeleteMapping("/Delete/{accessId}")
    public ResponseEntity<?> deleteAccess(@PathVariable Integer accessId, HttpServletRequest request){
        accessService.deleteAccess(accessId);
        Map<String,String> result = new HashMap<>();
        result.put("status_code","200");
        result.put("msg", "Access deleted!");
        return ok(result);
    }
}
