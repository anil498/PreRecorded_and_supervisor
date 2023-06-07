package com.VideoPlatform.Controller;

import com.VideoPlatform.Entity.*;
import com.VideoPlatform.Repository.*;
import com.VideoPlatform.Services.CommonService;
import com.VideoPlatform.Constant.RequestMappings;
import com.VideoPlatform.Services.UserServiceImpl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

import static org.springframework.http.ResponseEntity.ok;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping(RequestMappings.APICALLUSER)
public class UserController {

    private static final Logger logger= LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserServiceImpl userService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserAuthRepository userAuthRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    AccountAuthRepository accountAuthRepository;
    @Autowired
    AccountRepository accountRepository;
    @Autowired
    private AccessRepository accessRepository;
    @Autowired
    private CommonService commonService;

    @Value("${secret.key}")
    private String secret;
    @Value("${access.time}")
    private int accessTime;

    @GetMapping("/GetAll")

    public ResponseEntity<List<UserEntity>> getAllUsers(HttpServletRequest request){
        logger.info(commonService.getHeaders(request).toString());
        String authKey = request.getHeader("Authorization");
        String token = request.getHeader("Token");

        int authId = commonService.isValidAuthKey(authKey);
        if(authId == 0){
            logger.info("Unauthorised user, wrong authorization key !");
            return  new ResponseEntity<List<UserEntity>>(HttpStatus.UNAUTHORIZED);
        }

        if(!commonService.isValidToken(token,authId)) {
            logger.info("Invalid Token !");
            return  new ResponseEntity<List<UserEntity>>(HttpStatus.UNAUTHORIZED);
        }

        if(!(commonService.checkAccess("my_users",token))){
            logger.info("Permission Denied. Don't have access for this service!");
            return  new ResponseEntity<List<UserEntity>>(HttpStatus.UNAUTHORIZED);
        }

        return ok(userService.getAllUsers());
    }

    @GetMapping("/Child")
    public ResponseEntity<List<UserEntity>> getAllChild(HttpServletRequest request) {
        String authKey = request.getHeader("Authorization");
        String token = request.getHeader("Token");

        int authId = commonService.isValidAuthKey(authKey);
        if(authId == 0){
            logger.info("Unauthorised user, wrong authorization key !");
            return  new ResponseEntity<List<UserEntity>>(HttpStatus.UNAUTHORIZED);
        }

        if(!commonService.isValidToken(token,authId)) {
            logger.info("Invalid Token !");
            return  new ResponseEntity<List<UserEntity>>(HttpStatus.UNAUTHORIZED);
        }

        if(!(commonService.checkAccess("my_users",token))){
            logger.info("Permission Denied. Don't have access for this service!");
            return  new ResponseEntity<List<UserEntity>>(HttpStatus.UNAUTHORIZED);
        }
        UserAuthEntity user = userAuthRepository.findByToken(token);
        return ResponseEntity.ok(userService.getAllChild(user.getUserId()));
    }

    @GetMapping("/GetById/{id}")
    public ResponseEntity<UserEntity> getUserById(@PathVariable Integer id, HttpServletRequest request) {

        String authKey = request.getHeader("Authorization");
        String token = request.getHeader("Token");

        int authId = commonService.isValidAuthKey(authKey);
        if(authId == 0){
            logger.info("Unauthorised user, wrong authorization key !");
            return  new ResponseEntity<UserEntity>(HttpStatus.UNAUTHORIZED);
        }
        if(!commonService.isValidToken(token,authId)) {
            logger.info("Invalid Token !");
            return  new ResponseEntity<UserEntity>(HttpStatus.UNAUTHORIZED);
        }
        if(!(commonService.checkAccess("my_users",token))){
            logger.info("Permission Denied. Don't have access for this service!");
            return  new ResponseEntity<UserEntity>(HttpStatus.FORBIDDEN);
        }

            return ok(userService.getUserById(id));

    }

    @PostMapping("/Create")
    public ResponseEntity<?> createUser(@RequestBody UserEntity user, HttpServletRequest request) {

        String authKey = request.getHeader("Authorization");
        String token = request.getHeader("Token");

        int accountId = commonService.isValidAuthKeyU(authKey);
        if(accountId == 0){
            logger.info("Unauthorised user, wrong authorization key !");
            return  new ResponseEntity<UserEntity>(HttpStatus.UNAUTHORIZED);
        }
        int authId = commonService.isValidAuthKey(authKey);
        if(!commonService.isValidToken(token,authId)) {
            logger.info("Invalid Token !");
            return  new ResponseEntity<UserEntity>(HttpStatus.UNAUTHORIZED);
        }
        if(!(commonService.checkAccess("user_creation",token))){
            logger.info("Permission Denied. Don't have access for this service!");
            return  new ResponseEntity<UserEntity>(HttpStatus.UNAUTHORIZED);
        }
        AccountEntity a = accountRepository.findByAccountId(accountId);
        int maxUsers = a.getMaxUser();

        if(maxUsers == 0){
            logger.info("No more users are allowed, max limit exceed..!");
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        userService.createUser(user,authKey,token);

        Map<String,String> result = new HashMap<>();
        result.put("status_code ","200");
        result.put("msg", "User created!");
        return ok(result);

    }

    @PutMapping("/Update")
    public ResponseEntity<?> updateUser(@RequestBody UserEntity user, HttpServletRequest request) {

        String authKey = request.getHeader("Authorization");
        String token = request.getHeader("Token");

        int authId = commonService.isValidAuthKey(authKey);
        if(authId == 0){
            logger.info("Unauthorised user, wrong authorization key !");
            return  new ResponseEntity<UserEntity>(HttpStatus.UNAUTHORIZED);
        }
        if(!commonService.isValidToken(token,authId)) {
            logger.info("Invalid Token !");
            return  new ResponseEntity<UserEntity>(HttpStatus.UNAUTHORIZED);
        }
        if(!(commonService.checkAccess("user_update",token))){
            logger.info("Permission Denied. Don't have access for this service!");
            return  new ResponseEntity<UserEntity>(HttpStatus.UNAUTHORIZED);
        }
        userService.updateUser(user);
        Map<String,String> result = new HashMap<>();
        result.put("status_code ","200");
        result.put("msg", "User updated!");
        return ok(result);
    }

    @DeleteMapping("/Delete/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Integer id, HttpServletRequest request) {

        String authKey = request.getHeader("Authorization");
        String token = request.getHeader("Token");

        int authId = commonService.isValidAuthKey(authKey);
        if(authId == 0){
            logger.info("Unauthorised user, wrong authorization key !");
            return  new ResponseEntity<UserEntity>(HttpStatus.UNAUTHORIZED);
        }
        if(!commonService.isValidToken(token,authId)) {
            logger.info("Invalid Token !");
            return  new ResponseEntity<UserEntity>(HttpStatus.UNAUTHORIZED);
        }
        if(!(commonService.checkAccess("user_delete",token))){
            logger.info("Permission Denied. Don't have access for this service!");
            return  new ResponseEntity<UserEntity>(HttpStatus.UNAUTHORIZED);
        }
        userService.deleteUser(id);

        Map<String,String> result = new HashMap<>();
        result.put("status_code ","200");
        result.put("msg", "User deleted!");

        return ok(result);
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

}

