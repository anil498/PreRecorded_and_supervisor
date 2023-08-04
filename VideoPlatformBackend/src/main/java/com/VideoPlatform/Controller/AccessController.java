package com.VideoPlatform.Controller;

import com.VideoPlatform.Entity.UserEntity;
import com.VideoPlatform.Constant.RequestMappings;
import com.VideoPlatform.Entity.AccessEntity;
import com.VideoPlatform.Services.AccessService;
import com.VideoPlatform.Services.CommonService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    private static final Logger logger= LoggerFactory.getLogger(AccessController.class);
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
        return ok(commonService.responseData("200","Access Created!"));
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
    public ResponseEntity<?> updateAccess(@RequestBody String accessEntity, HttpServletRequest request){
        logger.info("REST API: PUT {} {} Request Headers={}", RequestMappings.APICALLACCESS, accessEntity != null ? accessEntity : "{}",commonService.getHeaders(request));
        if(accessEntity==null)
            return new ResponseEntity<>(commonService.responseData("500","Request must contain parameter values!"),HttpStatus.INTERNAL_SERVER_ERROR);

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
        return ok(commonService.responseData("200","Access Deleted!"));
    }
}
