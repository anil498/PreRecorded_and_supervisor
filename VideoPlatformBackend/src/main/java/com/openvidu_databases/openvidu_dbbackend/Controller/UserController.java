package com.openvidu_databases.openvidu_dbbackend.Controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.openvidu_databases.openvidu_dbbackend.Constant.RequestMappings;
import com.openvidu_databases.openvidu_dbbackend.Entity.*;
import com.openvidu_databases.openvidu_dbbackend.Exception.UserNotAuthorizedException;
import com.openvidu_databases.openvidu_dbbackend.Repository.*;
import com.openvidu_databases.openvidu_dbbackend.Services.UserService;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.SneakyThrows;
import org.apache.catalina.User;
import org.apache.commons.lang3.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.persistence.criteria.CriteriaBuilder;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.IntStream;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping(RequestMappings.APICALLUSER)
public class UserController {

    private static final Logger logger= LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserAuthRepository userAuthRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    AccountAuthRepository accountAuthRepository;

    @Autowired
    private FeatureRepository featureRepository;

    @Autowired
    private AccessRepository accessRepository;

    @Value("${secret.key}")
    private String secret;

    @Value("${access.time}")
    private int accessTime;

    private static final String DATE_FORMATTER= "yyyy-MM-dd HH:mm:ss";
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_FORMATTER);

    @GetMapping("/getAll")
    public ResponseEntity<List<UserEntity>> getAllUsers(HttpServletRequest request) {
        logger.info(getHeaders(request).toString());
        String authKey = request.getHeader("Authorization");
        String token = request.getHeader("Token");
        int authId = isValidAuthKey(authKey);
        logger.info("Auth Id .. "+authId);
        if(authId == 0){
            return  new ResponseEntity<List<UserEntity>>(HttpStatus.UNAUTHORIZED);
        }
        if (isValidToken(authId,token)) {
            return ResponseEntity.ok(userService.getAllUsers());
        }
        else {
            return  new ResponseEntity<List<UserEntity>>(HttpStatus.UNAUTHORIZED);
        }
    }

//    @GetMapping("/child/{id}")
//    public ResponseEntity<List<UserEntity>> getAllChildById(@PathVariable Integer id, HttpServletRequest request) {
//
//        int accId = Integer.parseInt(request.getHeader("accId"));
//        String authKey = request.getHeader("authKey");
//        Integer ID = Integer.valueOf(request.getHeader("userId"));
//        String token = request.getHeader("token");
//        logger.info(String.valueOf(ID));
//        logger.info(token);
//        if (isValidAuthKey(accId,authKey) && isValidToken(ID,token)) {
//            return ResponseEntity.ok(userService.getAllChild(id));
//        }
//        else{
//            return  new ResponseEntity<List<UserEntity>>(HttpStatus.UNAUTHORIZED);
//        }
//    }

    @GetMapping("/getById/{id}")
    public ResponseEntity<UserEntity> getUserById(@PathVariable Integer id, HttpServletRequest request) {

        String authKey = request.getHeader("Authorization");
        String token = request.getHeader("Token");
        int authId = isValidAuthKey(authKey);
        logger.info("Auth Id .. "+authId);
        if(authId == 0){
            return  new ResponseEntity<UserEntity>(HttpStatus.UNAUTHORIZED);
        }
        if (isValidToken(authId,token)) {
            logger.info(String.valueOf(userService.getUserById(id)));
            return ResponseEntity.ok(userService.getUserById(id));
        }
        else{
            return  new ResponseEntity<UserEntity>(HttpStatus.UNAUTHORIZED);
        }
    }

    @PostMapping("/create")
    public ResponseEntity<?> createUser(@RequestBody UserEntity user, HttpServletRequest request, HttpServletResponse response) throws JsonProcessingException {

        String authKey = request.getHeader("Authorization");
        String token = request.getHeader("Token");

        int authId = isValidAuthKey(authKey);
        if(authId == 0){
            logger.info("Unauthorised user, wrong authorization key !");
            return  new ResponseEntity<UserEntity>(HttpStatus.UNAUTHORIZED);
        }
        if(!(byAccess(authId,1001) && byAccess(authId,1006))){
            logger.info("for 1001 : "+byAccess(authId,1001));
            logger.info("for 1006 : "+byAccess(authId,1006));
            logger.info("Permission Denied. Don't have access for this service!");
            return  new ResponseEntity<UserEntity>(HttpStatus.UNAUTHORIZED);
        }
        if(!isValidToken(authId,token)) {
            logger.info("Invalid Token !");
            return  new ResponseEntity<UserEntity>(HttpStatus.UNAUTHORIZED);
        }

        AccountAuthEntity acc = accountAuthRepository.findByAuthKey(authKey);
        String creation = LocalDateTime.now().format(formatter);
        user.setAccountId(acc.getAccountId());
        user.setCreationDate(creation);
        String mypass = passwordEncoder.encode(user.getPassword());
        user.setPassword(mypass);
        Map<String,String> result = new HashMap<>();
        userService.createUser(user);
        result.put("status_code ","200");
        result.put("message", "User Created");
        return ResponseEntity.ok(result);

    }

    @PutMapping("/update/{id}")
    public ResponseEntity<UserEntity> updateUser(@PathVariable Integer id, @RequestBody UserEntity user, HttpServletRequest request) throws JsonProcessingException {

        String authKey = request.getHeader("Authorization");
        String token = request.getHeader("Token");

        int authId = isValidAuthKey(authKey);
        if(authId == 0){
            logger.info("Unauthorised user, wrong authorization key !");
            return  new ResponseEntity<UserEntity>(HttpStatus.UNAUTHORIZED);
        }
        if(!(byAccess(authId,1001) && byAccess(authId,1006))){
            logger.info("for 1001 : "+byAccess(authId,1001));
            logger.info("for 1006 : "+byAccess(authId,1006));
            logger.info("Permission Denied. Don't have access for this service!");
            return  new ResponseEntity<UserEntity>(HttpStatus.UNAUTHORIZED);
        }
        if(!isValidToken(authId,token)) {
            logger.info("Invalid Token !");
            return  new ResponseEntity<UserEntity>(HttpStatus.UNAUTHORIZED);
        }
        return ResponseEntity.ok(userService.updateUser(user, id));
    }
    @PostMapping("/up")
    public ResponseEntity<UserEntity> update(@RequestBody Map<String, String> params, HttpServletRequest request) {
        int loginId = Integer.parseInt(params.get("loginId"));
        String lastlogin = LocalDateTime.now().format(formatter);
        userRepository.findByUserId(loginId);
            userRepository.setLogin(lastlogin,loginId);

            return  null;
        }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Integer id, @RequestBody UserEntity user, HttpServletRequest request) throws JsonProcessingException {

        String authKey = request.getHeader("Authorization");
        String token = request.getHeader("Token");

        int authId = isValidAuthKey(authKey);
        if(authId == 0){
            logger.info("Unauthorised user, wrong authorization key !");
            return  new ResponseEntity<UserEntity>(HttpStatus.UNAUTHORIZED);
        }
        if(!(byAccess(authId,1001) && byAccess(authId,1006))){
            logger.info("for 1001 : "+byAccess(authId,1001));
            logger.info("for 1006 : "+byAccess(authId,1006));
            logger.info("Permission Denied. Don't have access for this service!");
            return  new ResponseEntity<UserEntity>(HttpStatus.UNAUTHORIZED);
        }
        if(!isValidToken(authId,token)) {
            logger.info("Invalid Token !");
            return  new ResponseEntity<UserEntity>(HttpStatus.UNAUTHORIZED);
        }
        return ResponseEntity.ok(userService.deleteUser(id));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> params,HttpServletRequest request) throws JsonProcessingException {
        String authKey = request.getHeader("Authorization");
        int authId = isValidAuthKey(authKey);
        logger.info("Auth Id .. "+authId);
        if(authId == 0){
            return  new ResponseEntity<UserEntity>(HttpStatus.UNAUTHORIZED);
        }
        String loginId = params.get("loginId");
        String password = params.get("password");

        logger.info("loginId : "+loginId);
        logger.info("password : "+password);

        UserEntity user1 = userRepository.findById(loginId);
        logger.info("user "+user1);
        int userId = user1.getUserId();
        logger.info("userId "+userId);
        UserAuthEntity user = userAuthRepository.findByAuthId(userId);
        logger.info("userId"+userId);

        if (user1 != null && passwordEncoder.matches(password,user1.getPassword()) && user1.getLoginId().equals(loginId)) {
            logger.info("Inside first if ...");
            if(isValidTokenLogin(userId)){
                logger.info("Inside second if ...");
                ObjectMapper obj = new ObjectMapper();
                String res = obj.writeValueAsString(user1);
                HashMap<String,String> response=new HashMap<>();
                response.put("token",user.getToken());
                response.put("user_data",res);
                response.put("status_code","200");
                response.put("status_message","Login Successful");
                response.put("Features", String.valueOf(byFeature(user1.getUserId())));
//                String lastlogin = LocalDateTime.now().format(formatter);
        //        user1.setLastLogin(lastlogin);
        //        userRepository.setLastLogin(id);
 //               logger.info("last login :"+lastlogin);
              //  logger.info("UE :"+u1);
 //               userRepository.setLogin(lastlogin,userId);
 //               logger.info("Last Login : "+user1.getLastLogin());
                return ResponseEntity.ok(response);
            }
            else {

                if (user1 != null && passwordEncoder.matches(password,user1.getPassword())) {
                    String token1 = generateToken(userId,"UR");
                    LocalDateTime now = LocalDateTime.now();
                    LocalDateTime newDateTime = now.plus(accessTime, ChronoUnit.HOURS);
                    UserAuthEntity ua = userAuthRepository.findByAuthId(userId);
                    String lastlogin = LocalDateTime.now().format(formatter);
                //    logger.info("last login1 :"+lastlogin);
                //    logger.info("UE :"+u1);
                //    userRepository.setLogin(lastlogin,userId);
                 //   logger.info("Last Login : "+user1.getLastLogin());

                    if(ua != null){
                        ua.setToken(token1);
                        ua.setAuthId(authId);
                        ua.setCreationDate(now);
                        ua.setExpDate(newDateTime);
                    }
                    else{
                        ua = new UserAuthEntity();
                        ua.setLoginId(user1.getLoginId());
                        ua.setUserId(user1.getUserId());
                        ua.setToken(token1);
                        ua.setAuthId(authId);
                        ua.setCreationDate(now);
                        ua.setExpDate(newDateTime);
                    }

                    userAuthRepository.save(ua);
                    Map<String,String> res = new HashMap<>();
                    res.put("token",token1);
                    res.put("user_data",user1.toString());
                    res.put("status_code","200");
                    res.put("status_message","Login Successful");
                    res.put("Features", String.valueOf(byFeature(user1.getUserId())));
                    logger.info(user1.toString());
                    return new ResponseEntity<>(res, HttpStatus.OK);
                }
     //      }
        }}
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    private String generateToken(Integer userId,String type) {
        String val = String.format("%04d", userId);
        logger.info("Format val = "+val);
        return type+val+givenUsingApache_whenGeneratingRandomAlphanumericString_thenCorrect();
    }

    public String givenUsingApache_whenGeneratingRandomAlphanumericString_thenCorrect() {
        String generatedString = RandomStringUtils.randomAlphanumeric(10);
        logger.info(generatedString);
        System.out.println(generatedString);
        return generatedString;
    }

    public int isValidAuthKey(String authKey){
        AccountAuthEntity acc = accountAuthRepository.findByAuthKey(authKey);
        if(acc == null)return 0;
        String key = (acc.getAuthKey());
        if(acc == null || acc.getExpDate().isBefore(LocalDateTime.now()) || authKey == null || !(key.equals(authKey))){
            return 0;
        }
        int authId = acc.getAuthId();
        return authId;
    }
    public Boolean isValidToken(int authId,String token){
        UserAuthEntity user = userAuthRepository.findByAuthId(authId);
        if(user == null)return false;
        logger.info("DAta by authId.."+user);
        //logger.info(user.getToken());
        String t = (user.getToken());
        if(user == null || user.getExpDate().isBefore(LocalDateTime.now()) || token == null || !(t.equals(token)))
            return false;
        return true;
    }
    public Boolean isValidTokenLogin(int id){

        UserAuthEntity user = userAuthRepository.findByUId(id);
        if(user == null || user.getExpDate().isBefore(LocalDateTime.now()) ) {
            return false;
        }
        return true;
    }

   private Map<String, String> getHeaders(HttpServletRequest request) {
       Enumeration<String> headers = request.getHeaderNames();
       Map<String, String> headerMap = new HashMap<>();
       while (headers.hasMoreElements()) {
           String header = headers.nextElement();
           headerMap.put(header, request.getHeader(header));
       }
       return headerMap;
   }

   private String byFeature(Integer userId) throws JsonProcessingException {

        UserEntity user = userRepository.findByUserId(userId);

        ObjectMapper obj = new ObjectMapper();
        String str = obj.writeValueAsString(user.getFeatures());
        logger.info("Val : "+str);
        String[] string = str.replaceAll("\\[", "")
               .replaceAll("]", "")
               .split(",");
        int[] arr = new int[string.length];
        Map<Integer,String> res = new HashMap<>();
        for (int i = 0; i < string.length; i++) {
           arr[i] = Integer.valueOf(string[i]);
        }
        ObjectMapper obj1 = new ObjectMapper();
       String str1="";
        for (int i = 0; i < arr.length; i++) {
             str1 += obj.writeValueAsString(featureRepository.findById(arr[i]))+",";
        }
        logger.info("Response : "+res);
        logger.info("String : " + str);
        logger.info("\nInteger array : "
               + Arrays.toString(arr));
        return str1;
   }

    private boolean byAccess(Integer authId,int toCheckValue) throws JsonProcessingException {

        UserAuthEntity user = userAuthRepository.findByAuthId(authId);
        int userId = user.getUserId();
        UserEntity u = userRepository.findByUserId(userId);
        if(user == null) return false;
        ObjectMapper obj = new ObjectMapper();
        String str = obj.writeValueAsString(u.getAccessId());
        logger.info("Val : "+str);
        String[] string = str.replaceAll("\\[", "")
                .replaceAll("]", "")
                .split(",");
        int[] arr = new int[string.length];
        int[] apiArr = new int[string.length];
        for (int i = 0; i < string.length; i++) {
            arr[i] = Integer.valueOf(string[i]);
        }
        int size = arr.length;
        for (int i = 0; i < arr.length; i++) {
            AccessEntity access = accessRepository.findById(arr[i]);
            int apiId = access.getApiId();
            apiArr[i] = apiId;
        }

        boolean apiPresent  = ifExistInApiArray(apiArr,toCheckValue);
        logger.info("IfAPIPresent.... : "+apiPresent);
        logger.info("String : " + str);
        logger.info("Integer array : " + Arrays.toString(arr));
        logger.info("API array : " + Arrays.toString(apiArr));
        return apiPresent;

    }

    private boolean ifExistInApiArray(int[] arr,int toCheckValue){
        logger.info("ToCheck Array is : "+arr);
        logger.info("TocheckVal is : "+toCheckValue);
        return IntStream.of(arr).anyMatch(x -> x == toCheckValue);
    }
    private boolean ifExistInAccessArray(int[] arr,int toCheckValue){
        boolean test
                = Arrays.asList(arr)
                .contains(toCheckValue);
        return test;
    }

}

