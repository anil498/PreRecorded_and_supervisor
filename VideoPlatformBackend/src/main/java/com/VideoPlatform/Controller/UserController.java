package com.VideoPlatform.Controller;

import com.VideoPlatform.Entity.*;
import com.VideoPlatform.Repository.*;
import com.VideoPlatform.Services.AccountService;
import com.VideoPlatform.Services.CommonService;
import com.VideoPlatform.Constant.RequestMappings;
import com.VideoPlatform.Services.UserService;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;

import static org.springframework.http.ResponseEntity.ok;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping(RequestMappings.APICALLUSER)
public class UserController {

    private static final Logger logger= LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserService userService;
    @Autowired
    private AccountService accountService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private CommonService commonService;

    @Value("${file.path}")
    private String FILE_DIRECTORY;

    @GetMapping("/GetAll")
    public ResponseEntity<?> getAllUsers(HttpServletRequest request){
        logger.info(commonService.getHeaders(request).toString());
        String authKey = request.getHeader("Authorization");
        String token = request.getHeader("Token");
        if(commonService.authorizationCheck(authKey,token,"get_all")){
            return ok(userService.getAllUsers());
        }
        if(commonService.authorizationCheck(authKey,token,"my_users")){
            return ok(userService.getAllChild(token));
        }
        return  new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    }

//    @GetMapping("/Child")
//    public ResponseEntity<List<UserEntity>> getAllChild(HttpServletRequest request) {
//        String authKey = request.getHeader("Authorization");
//        String token = request.getHeader("Token");
//        if(!commonService.authorizationCheck(authKey,token,"my_users")){
//            return  new ResponseEntity<List<UserEntity>>(HttpStatus.UNAUTHORIZED);
//        }
//        return ResponseEntity.ok(userService.getAllChild(token));
//    }

    @GetMapping("/GetById/{id}")
    public ResponseEntity<?> getUserById(@PathVariable Integer id, HttpServletRequest request) {

        String authKey = request.getHeader("Authorization");
        String token = request.getHeader("Token");
        if(!commonService.authorizationCheck(authKey,token,"my_users")){
            return  new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        return ok(userService.getUserById(id));
    }

    @PostMapping("/Create")
    public ResponseEntity<?> createUser(@RequestBody String userEntity, HttpServletRequest request) throws JsonProcessingException {

        String authKey = request.getHeader("Authorization");
        String token = request.getHeader("Token");
        if(!commonService.authorizationCheck(authKey,token,"user_creation")){
            return  new ResponseEntity<UserEntity>(HttpStatus.UNAUTHORIZED);
        }
        return userService.createUser(userEntity,authKey,token);
    }

    @PutMapping("/Update")
    public ResponseEntity<?> updateUser(@RequestBody String params1, HttpServletRequest request) {

        String authKey = request.getHeader("Authorization");
        String token = request.getHeader("Token");
        if(!commonService.isValidRequestUserUpdate(authKey,token,"user_update","customer_creation")){
            return  new ResponseEntity<UserEntity>(HttpStatus.UNAUTHORIZED);
        }
        return userService.updateUser(params1,authKey);
    }

    @DeleteMapping("/Delete/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Integer id, HttpServletRequest request) {

        String authKey = request.getHeader("Authorization");
        String token = request.getHeader("Token");
        if(!commonService.authorizationCheck(authKey,token,"user_delete")){
            return  new ResponseEntity<UserEntity>(HttpStatus.UNAUTHORIZED);
        }


        return userService.deleteUser(id);
    }

    @PostMapping("/getVideo")
    public ResponseEntity<?> handleFileUpload(@RequestParam(value = "prerecorded_video_file", required = false) MultipartFile file,HttpServletRequest request) throws IOException {
        String loginId = request.getHeader("loginId");
        String name = request.getHeader("name");
        return userService.handleFileUpload(loginId,name,file);
    }

    @PostMapping("/getImage")
    public ResponseEntity<?> getImage(@RequestBody Map<String, Object> params) throws IOException {

        Map<String,String> encodedImage = userService.getImage(params);
        if(encodedImage==null){
            return new ResponseEntity<>("Image encoding failed!",HttpStatus.FORBIDDEN);
        }
        return new ResponseEntity<>(encodedImage,HttpStatus.OK);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> params,HttpServletRequest request) {
        String authKey = request.getHeader("Authorization");
        int authId = commonService.isValidAuthKey(authKey);
        logger.info("Auth Id .. " + authId);
        if (authId == 0) {
            return new ResponseEntity<UserEntity>(HttpStatus.UNAUTHORIZED);
        }

        String loginId = params.get("loginId");
        String password = params.get("password");

        return userService.loginService(loginId, password, authId);
    }

    @PostMapping("/checkLoginId")
    public ResponseEntity<?> checkLogin(@RequestBody Map<String, String> params,HttpServletRequest request){
        String loginId = params.get("loginId");
        if(!userService.checkLoginId(loginId)){
            return new ResponseEntity<UserEntity>(HttpStatus.UNAUTHORIZED);
        }
        Map<String,String> result = new HashMap<>();
        result.put("status_code","200");
        result.put("msg", "Valid Login Id !");
        return ok(result);
    }
//    @PostMapping("/ResetPassword")
//    public ResponseEntity<?> resetPassword(@RequestBody Map<String, String>params,HttpServletRequest request){
//        String authKey = request.getHeader("Authorization");
//        String token = request.getHeader("Token");
//
//        if(!commonService.authorizationCheck(authKey,token)){
//            return  new ResponseEntity<UserEntity>(HttpStatus.UNAUTHORIZED);
//        }
//        if(!(commonService.checkAccess("user_delete",token))){
//            logger.info("Permission Denied. Don't have access for this service!");
//            return  new ResponseEntity<UserEntity>(HttpStatus.UNAUTHORIZED);
//        }
//        String newPassword  = params.get("password");
//        Integer userId = Integer.valueOf(params.get("userId"));
//        String loginId = params.get("loginId");
//        if(userService.resetPassword(newPassword,loginId,userId)==null)
//            return new ResponseEntity<UserEntity>(HttpStatus.UNAUTHORIZED);
//        Map<String,String> result = new HashMap<>();
//        result.put("status_code ","200");
//        result.put("msg", "Password reset successfully!");
//        return ok(result);
//    }

}

