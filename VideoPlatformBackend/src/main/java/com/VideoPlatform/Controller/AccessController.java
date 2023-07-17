package com.VideoPlatform.Controller;

import com.VideoPlatform.Entity.UserEntity;
import com.VideoPlatform.Constant.RequestMappings;
import com.VideoPlatform.Entity.AccessEntity;
import com.VideoPlatform.Services.AccessService;
import com.VideoPlatform.Services.CommonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

import static org.springframework.http.ResponseEntity.ok;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping(RequestMappings.APICALLACCESS)
public class AccessController {
    @Autowired
    private CommonService commonService;
    @Autowired
    private AccessService accessService;

    @PostMapping("/Create")
    public ResponseEntity<?> createUser(@RequestBody AccessEntity accessEntity, HttpServletRequest request) {
        String authKey = request.getHeader("Authorization");
        String token = request.getHeader("Token");

        if(!commonService.authorizationCheck(authKey,token,"manage_platform_access")){
            return  new ResponseEntity<UserEntity>(HttpStatus.UNAUTHORIZED);
        }
        if(accessService.createAccess(accessEntity,authKey,token)==null){
            return  new ResponseEntity<>("Access Entity already exist !",HttpStatus.UNAUTHORIZED);
        }
        Map<String,String> result = new HashMap<>();
        result.put("status_code","200");
        result.put("msg", "Access created!");
        return ok(result);
    }

    @GetMapping("/GetAll")
    public ResponseEntity<?> getAllAccess(HttpServletRequest request){
        String authKey = request.getHeader("Authorization");
        String token = request.getHeader("Token");
        if(!commonService.authorizationCheck(authKey,token,"manage_platform_access")){
            return  new ResponseEntity<UserEntity>(HttpStatus.UNAUTHORIZED);
        }
        return ok(accessService.getAllAccess());
    }

    @PutMapping("/Update")
    public ResponseEntity<?> updateUser(@RequestBody String accessEntity, HttpServletRequest request){
        String authKey = request.getHeader("Authorization");
        String token = request.getHeader("Token");

        if(!commonService.authorizationCheck(authKey,token,"manage_platform_access")){
            return  new ResponseEntity<UserEntity>(HttpStatus.UNAUTHORIZED);
        }
        return accessService.updateAccess(accessEntity);
    }

    @DeleteMapping("/Delete/{accessId}")
    public ResponseEntity<?> deleteAccess(@PathVariable Integer accessId, HttpServletRequest request){
        String authKey = request.getHeader("Authorization");
        String token = request.getHeader("Token");

        if(!commonService.authorizationCheck(authKey,token,"manage_platform_access")){
            return  new ResponseEntity<UserEntity>(HttpStatus.UNAUTHORIZED);
        }
        accessService.deleteAccess(accessId);
        Map<String,String> result = new HashMap<>();
        result.put("status_code","200");
        result.put("msg", "Access deleted!");
        return ok(result);
    }
}
