package com.VideoPlatform.Controller;

import com.VideoPlatform.Entity.*;
import com.VideoPlatform.Repository.*;
import com.VideoPlatform.Services.AccountService;
import com.VideoPlatform.Services.CommonService;
import com.VideoPlatform.Constant.RequestMappings;
import com.VideoPlatform.Services.UserService;

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
    @Value("${file.path}")
    private String FILE_DIRECTORY;

    @GetMapping("/GetAll")
    public ResponseEntity<List<UserEntity>> getAllUsers(HttpServletRequest request){
        logger.info(commonService.getHeaders(request).toString());
        String authKey = request.getHeader("Authorization");
        String token = request.getHeader("Token");
        if(!commonService.authorizationCheck(authKey,token,"my_users")){
            return  new ResponseEntity<List<UserEntity>>(HttpStatus.UNAUTHORIZED);
        }
        return ok(userService.getAllUsers());
    }

    @GetMapping("/Child")
    public ResponseEntity<List<UserEntity>> getAllChild(HttpServletRequest request) {
        String authKey = request.getHeader("Authorization");
        String token = request.getHeader("Token");
        if(!commonService.authorizationCheck(authKey,token,"my_users")){
            return  new ResponseEntity<List<UserEntity>>(HttpStatus.UNAUTHORIZED);
        }
        return ResponseEntity.ok(userService.getAllChild(token));
    }

    @GetMapping("/GetById/{id}")
    public ResponseEntity<UserEntity> getUserById(@PathVariable Integer id, HttpServletRequest request) {

        String authKey = request.getHeader("Authorization");
        String token = request.getHeader("Token");
        if(!commonService.authorizationCheck(authKey,token,"my_users")){
            return  new ResponseEntity<UserEntity>(HttpStatus.UNAUTHORIZED);
        }
        return ok(userService.getUserById(id));
    }

    @PostMapping("/Create")
    public ResponseEntity<?> createUser(@RequestBody UserEntity userEntity, HttpServletRequest request) {

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
        userService.deleteUser(id);
        Map<String,String> result = new HashMap<>();
        result.put("status_code","200");
        result.put("msg", "User deleted!");
        return ok(result);
    }

    @PostMapping("/getVideo")
    public ResponseEntity<String> handleFileUpload(@RequestParam(value = "prerecorded_video_file", required = false) MultipartFile file,HttpServletRequest request) throws IOException {
        String loginId = request.getHeader("loginId");
        String name = request.getHeader("name");
        logger.info("loginId : {}",loginId);
        if(request.getHeader("name").isEmpty() && request.getHeader("loginId").isEmpty()){
            return new ResponseEntity<String>("Invalid credentials !",HttpStatus.UNAUTHORIZED);
        }
        System.out.println(file);
        try{
            if(file.isEmpty()){
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Request must contain file");
            }
        }
        catch(Exception e){
        }
        if(request.getHeader("name").isEmpty() && request.getHeader("loginId").isEmpty()){
            return new ResponseEntity<>("Must contain loginId or name",HttpStatus.UNAUTHORIZED);
        }
        String fileName="";
        String filePathA="";
        String filePathU="";
        try {
            Path path = Paths.get(FILE_DIRECTORY);
            if (!Files.exists(path)) {
                logger.info("Data/Prerecorded does not exist, creating...");
                Files.createDirectories(path);
            }
            if(!request.getHeader("loginId").isEmpty() && !request.getHeader("name").isEmpty()){
                logger.info("New Account Creation Case!");
                UserEntity userEntity = userRepository.findByLoginId(loginId);
                Integer userId = userEntity.getUserId();
                Integer accountId = userEntity.getAccountId();
                Path path1 = Paths.get(FILE_DIRECTORY +"/"+accountId+"/"+userId+"/");
                Path path2 = Paths.get(FILE_DIRECTORY +"/"+accountId+"/");
                if (!Files.exists(path1)) {
                    Files.createDirectories(path1);
                }
                Files.copy(file.getInputStream(), Paths.get(String.valueOf(path1),file.getOriginalFilename()), StandardCopyOption.REPLACE_EXISTING);
                Files.copy(file.getInputStream(), Paths.get(String.valueOf(path2),file.getOriginalFilename()), StandardCopyOption.REPLACE_EXISTING);
                filePathA = String.valueOf(path2);
                filePathU = String.valueOf(path1);
            }
            else if (!request.getHeader("loginId").isEmpty()) {
                logger.info("User Creation or update!");
                UserEntity userEntity = userRepository.findByLoginId(loginId);
                Integer userId = userEntity.getUserId();
                Integer accountId = userEntity.getAccountId();
                path = Paths.get(FILE_DIRECTORY +"/"+accountId+"/"+userId+"/");
                if (!Files.exists(path)) {
                    Files.createDirectories(path);
                }
                Files.copy(file.getInputStream(), Paths.get(String.valueOf(path),file.getOriginalFilename()), StandardCopyOption.REPLACE_EXISTING);
                filePathU = String.valueOf(path);
            }
            else if (!request.getHeader("name").isEmpty()) {
                logger.info("Account creation or update!");
                AccountEntity accountEntity = accountRepository.findByAccountName(name);
                Integer accountId = accountEntity.getAccountId();
                path = Paths.get(FILE_DIRECTORY+"/"+accountId+"/");
                if (!Files.exists(path)) {
                    Files.createDirectories(path);
                }
                Files.copy(file.getInputStream(), Paths.get(String.valueOf(path),file.getOriginalFilename()), StandardCopyOption.REPLACE_EXISTING);
                filePathA = String.valueOf(path);
            }
            fileName = file.getOriginalFilename();
            logger.info("File Path : {}",(path));
            logger.info("Path : {}",path);
           // logger.info("fileName : {}",fileName);

        }catch (Exception e){
            logger.info("Exception while uploading file is : ",e);
        }

        logger.info("loginId : {}",loginId);

        if(request.getHeader("name").isEmpty()){
            userService.saveFilePathToFeature(filePathU+fileName,loginId,name);
        }
        else if(request.getHeader("loginId").isEmpty()){
            accountService.saveFilePathToFeature(filePathA+fileName,loginId,name);
        }
        else{
            userService.saveFilePathToFeature(filePathU+fileName,loginId,name);
            accountService.saveFilePathToFeature(filePathA+fileName,loginId,name);
        }
        return ResponseEntity.ok("File Uploaded Successfully");
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

